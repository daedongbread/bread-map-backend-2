package com.depromeet.breadmapbackend.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.dto.AlarmDto;
import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.user.dto.ProfileDto;
import com.depromeet.breadmapbackend.domain.user.dto.UpdateNickNameRequest;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final NoticeTokenRepository noticeTokenRepository;
	private final UserCacheRepository userCacheRepository;

	@Override
	public CurrentUserInfo loadUserInfoByOAuthId(final String oAuthId) {
		return userCacheRepository.getUser(oAuthId).orElseGet(() -> {
			final CurrentUserInfo userInfo =
				CurrentUserInfo.of(userRepository.findByOAuthId(oAuthId)
					.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND)));
			userCacheRepository.setIfAbsent(userInfo);
			return userInfo;
		});
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public ProfileDto profile(String oAuthId, Long userId) {
		User me = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User other = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Integer followingNum = followRepository.countByFromUser(other);
		Integer followerNum = followRepository.countByToUser(other);
		Boolean isFollow = followRepository.findByFromUserAndToUser(me, other).isPresent();

		return ProfileDto.builder()
			.user(other)
			.followingNum(followingNum)
			.followerNum(followerNum)
			.isFollow(isFollow)
			.build();
	}

	public void updateNickName(String oAuthId, UpdateNickNameRequest request) {
		User me = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (request.getNickName() != null) {
			Optional<User> user = userRepository.findByNickName(request.getNickName());
			if (user.isEmpty())
				me.getUserInfo().updateNickName(request.getNickName());
			else if (!me.equals(user.get()))
				throw new DaedongException(DaedongStatus.NICKNAME_DUPLICATE_EXCEPTION);
		}

		if (request.getImage() != null)
			me.getUserInfo().updateImage(request.getImage());
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public AlarmDto getAlarmStatus(String oAuthId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		return new AlarmDto(user.getIsAlarmOn());
	}

	public AlarmDto alarmChange(String oAuthId, NoticeTokenRequest request) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (request.isNoticeAgree()) {
			if (noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isEmpty()) {
				noticeTokenRepository.save(
					NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build());
			}
			return new AlarmDto(user.alarmOn());
		} else {
			noticeTokenRepository.deleteAllInBatch(
				noticeTokenRepository.findAllByDeviceToken(request.getDeviceToken())
			);

			return new AlarmDto(user.alarmOff());
		}
	}

}
