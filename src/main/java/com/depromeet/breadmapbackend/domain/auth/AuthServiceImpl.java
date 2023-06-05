package com.depromeet.breadmapbackend.domain.auth;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.auth.dto.LoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.RegisterRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.security.token.OIDCVerifyProcessor;
import com.depromeet.breadmapbackend.global.security.token.RedisTokenUtils;
import com.depromeet.breadmapbackend.global.security.userinfo.OIDCUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final NoticeTokenRepository noticeTokenRepository;
	private final FlagRepository flagRepository;
	private final RedisTokenUtils redisTokenUtils;
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomAWSS3Properties customAWSS3Properties;
	private final OIDCVerifyProcessor oidcVerifyProcessor;

	@Transactional(rollbackFor = Exception.class)
	public JwtToken login(LoginRequest request) {
		final OIDCUserInfo oidcUserInfo =
			oidcVerifyProcessor.verifyIdToken(request.getType(), request.getIdToken());

		User user = userRepository.findByOAuthId(oidcUserInfo.getOAuthId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		if (user.getUserInfo().getEmail() == null)
			user.getUserInfo().updateEmail(oidcUserInfo.getEmail());
		if (user.getIsBlock())
			throw new DaedongException(DaedongStatus.BLOCK_USER);

		return createNewToken(oidcUserInfo.getOAuthId(), RoleType.USER);
	}

	@Transactional(rollbackFor = Exception.class)
	public JwtToken register(RegisterRequest request) {
		final OIDCUserInfo oidcUserInfo =
			oidcVerifyProcessor.verifyIdToken(request.getType(), request.getIdToken());

		if (!redisTokenUtils.isRejoinPossible(oidcUserInfo.getOAuthId()))
			throw new DaedongException(DaedongStatus.REJOIN_RESTRICT);

		if (userRepository.findByOAuthId(oidcUserInfo.getOAuthId()).isEmpty()) {
			createUser(oidcUserInfo, request.getIsMarketingInfoReceptionAgreed());
			return createNewToken(oidcUserInfo.getOAuthId(), RoleType.USER);
		} else
			throw new DaedongException(DaedongStatus.ALREADY_REGISTER_USER);
	}

	private String createNickName() {
		List<String> adjectiveList =
			Arrays.asList("맛있는", "달콤한", "매콤한", "바삭한", "짭잘한", "고소한", "알싸한", "새콤한", "느끼한", "무서운");
		List<String> breadNameList =
			Arrays.asList("식빵", "소금빵", "바게트", "마카롱", "마늘빵", "베이글", "도넛", "꽈배기", "피자빵", "크루아상");

		String nickName;
		do {
			String adjective = adjectiveList.get(new SecureRandom().nextInt(adjectiveList.size()));
			String breadName = breadNameList.get(new SecureRandom().nextInt(breadNameList.size()));

			int num = new SecureRandom().nextInt(9999) + 1;

			nickName = adjective + breadName + num;
		} while (userRepository.findByNickName(nickName).isPresent());
		return nickName;
	}

	private void createUser(OIDCUserInfo oidcUserInfo, Boolean isMarketingInfoReceptionAgreed) {
		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder()
				.oAuthType(oidcUserInfo.getOAuthType())
				.oAuthId(oidcUserInfo.getOAuthId()).build())
			.userInfo(UserInfo.builder()
				.nickName(createNickName())
				.email(oidcUserInfo.getEmail())
				.image(customAWSS3Properties.getCloudFront() + "/" +
					customAWSS3Properties.getDefaultImage().getUser() + ".png").build())
			.isMarketingInfoReceptionAgreed(isMarketingInfoReceptionAgreed)
			.build();
		userRepository.save(user);

		Flag wantToGo = Flag.builder().user(user).name("가고싶어요").color(FlagColor.ORANGE).build();
		flagRepository.save(wantToGo);
		user.getFlagList().add(wantToGo);
		Flag alreadyGo = Flag.builder().user(user).name("가봤어요").color(FlagColor.ORANGE).build();
		flagRepository.save(alreadyGo);
		user.getFlagList().add(alreadyGo);
	}

	@Transactional(rollbackFor = Exception.class)
	public JwtToken reissue(ReissueRequest request) {
		if (!jwtTokenProvider.verifyToken(request.getRefreshToken()) ||
			!redisTokenUtils.isRefreshTokenValid(request.getRefreshToken(), request.getAccessToken()) ||
			redisTokenUtils.isBlackList(request.getAccessToken()))
			throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

		String oAuthId = redisTokenUtils.getOAuthIdFromRefreshToken(request.getRefreshToken());
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (user.getOAuthInfo().getOAuthType().equals(OAuthType.KAKAO) && user.getUserInfo().getEmail() == null) {
			makeRefreshTokenInvalid(request.getRefreshToken());
			throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);
		}
		JwtToken reissueToken = createNewToken(oAuthId, user.getRoleType());
		makeRefreshTokenInvalid(request.getRefreshToken()); // TODO : accessToken이 유효기간 남아 있으면?
		return reissueToken;
	}

	private JwtToken createNewToken(String oAuthId, RoleType roleType) {
		JwtToken jwtToken = jwtTokenProvider.createJwtToken(oAuthId, roleType.getCode());
		// key : refreshToken, value : oAuthId:accessToken
		redisTokenUtils.setRefreshToken(
			jwtToken.getRefreshToken(),
			oAuthId + ":" + jwtToken.getAccessToken(),
			jwtTokenProvider.getRefreshTokenExpiredDate()
		);
		return jwtToken;
	}

	@Transactional(rollbackFor = Exception.class)
	public void logout(LogoutRequest request) {  // TODO
		String oAuthId = jwtTokenProvider.getAuthentication(request.getAccessToken()).getName();

		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
			noticeTokenRepository.delete(
				noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get());
		}

		makeTokenInvalid(request.getAccessToken(), request.getRefreshToken());
	}

	private void makeTokenInvalid(String accessToken, String refreshToken) {
		redisTokenUtils.setAccessTokenBlackList(accessToken, jwtTokenProvider.getExpiration(accessToken));
		redisTokenUtils.deleteRefreshToken(refreshToken);
	}

	private void makeRefreshTokenInvalid(String refreshToken) {
		redisTokenUtils.deleteRefreshToken(refreshToken);
	}
}
