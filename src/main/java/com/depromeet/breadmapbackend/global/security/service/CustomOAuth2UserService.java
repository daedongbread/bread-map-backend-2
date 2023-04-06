package com.depromeet.breadmapbackend.global.security.service;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.domain.ProviderType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfo;
import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // OAuth 2 로그인 성공 이후 사용자 정보를 가져올 때의 설정
    private final UserRepository userRepository;
    private final FlagRepository flagRepository;
    private final StringRedisTemplate redisTemplate;
    private final NoticeTokenRepository noticeTokenRepository;
    private final CustomAWSS3Properties customAwss3Properties;
    private final CustomRedisProperties customRedisProperties;

    @Override
    // oAuth2UserRequest 에는 access token 과 같은 정보들
    // 서드파티에 사용자 정보를 요청할 수 있는 access token
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());

        String username = providerType.name() + "_" + oAuth2UserInfo.getUsername();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(customRedisProperties.getKey().getDelete() + ":" + username)))
            throw new DaedongException(DaedongStatus.REJOIN_RESTRICT);

        User user = null;
        if (userRepository.findByUsername(username).isPresent()) {
            user = userRepository.findByUsername(username).get();

            if (flagRepository.findByUserAndName(user, "가고싶어요").isEmpty()) {
                Flag wantToGo = Flag.builder().user(user).name("가고싶어요").color(FlagColor.ORANGE).build();
                flagRepository.save(wantToGo);
                user.addFlag(wantToGo);
            }
            if (flagRepository.findByUserAndName(user, "가봤어요").isEmpty()) {
                Flag alreadyGo = Flag.builder().user(user).name("가봤어요").color(FlagColor.ORANGE).build();
                flagRepository.save(alreadyGo);
                user.addFlag(alreadyGo);
            }
        } else {
            user = createUser(oAuth2UserInfo, providerType);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes()); // TODO
    }

    private String createNickName() {
        List<String> adjectiveList =
                Arrays.asList("맛있는", "달콤한", "매콤한", "바삭한", "짭잘한", "고소한", "알싸한", "새콤한", "느끼한", "무서운");
        List<String> breadNameList =
                Arrays.asList("식빵", "소금빵", "바게트", "마카롱", "마늘빵", "베이글", "도넛", "꽈배기", "피자빵", "크루아상");

        String nickName;
        do {
            Random rand = new Random();
            String adjective = adjectiveList.get(rand.nextInt(adjectiveList.size()));
            String breadName = breadNameList.get(rand.nextInt(breadNameList.size()));

            int num = rand.nextInt(9999) + 1;

            nickName = adjective + breadName + num;
        } while (userRepository.findByNickName(nickName).isPresent());
        return nickName;
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo, ProviderType providerType) {
        User user = User.builder()
                .username(providerType.name() + "_" + oAuth2UserInfo.getUsername())
                .nickName(createNickName())
                .email(oAuth2UserInfo.getEmail())
                .providerType(providerType)
                .roleType(RoleType.USER)
//                .image(oAuth2UserInfo.getImageUrl())
                .image(customAwss3Properties.getCloudFront() + "/" +
                        customAwss3Properties.getBucket() + "/" +
                        customAwss3Properties.getDefaultImage().getUser() + ".png")
                .build();
        userRepository.save(user);

        if (flagRepository.findByUserAndName(user, "가고싶어요").isEmpty()) {
            Flag wantToGo = Flag.builder().user(user).name("가고싶어요").color(FlagColor.ORANGE).build();
            flagRepository.save(wantToGo);
            user.addFlag(wantToGo);
        }
        if (flagRepository.findByUserAndName(user, "가봤어요").isEmpty()) {
            Flag alreadyGo = Flag.builder().user(user).name("가봤어요").color(FlagColor.ORANGE).build();
            flagRepository.save(alreadyGo);
            user.addFlag(alreadyGo);
        }
        return user;
    }

//    private User updateUser(User user, OAuth2UserInfo oAuth2UserInfo) {
//        if (oAuth2UserInfo.getNickName() != null && !user.getUsername().equals(oAuth2UserInfo.getNickName())) {
//            user.updateNickName(oAuth2UserInfo.getNickName());
//        }

//        if (oAuth2UserInfo.getImageUrl() != null && !user.getImage().equals(oAuth2UserInfo.getImageUrl())) {
//            user.updateImage(oAuth2UserInfo.getImageUrl());
//        }
//        return user;
//    }

}