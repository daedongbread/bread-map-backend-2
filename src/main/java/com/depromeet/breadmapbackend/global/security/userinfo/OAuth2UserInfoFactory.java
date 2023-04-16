package com.depromeet.breadmapbackend.global.security.userinfo;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.impl.AppleOAuth2UserInfo;
import com.depromeet.breadmapbackend.global.security.userinfo.impl.GoogleOAuth2UserInfo;
import com.depromeet.breadmapbackend.global.security.userinfo.impl.KakaoOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(OAuthType OAuthType, Map<String, Object> attributes) {
        switch (OAuthType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case APPLE: return new AppleOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("유효하지 않은 Provider 타입입니다.");
        }
    }

}
