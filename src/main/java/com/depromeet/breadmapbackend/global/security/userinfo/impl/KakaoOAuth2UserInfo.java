package com.depromeet.breadmapbackend.global.security.userinfo.impl;

import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsername() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getNickName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("account_email");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("thumbnail_image");
    }

}
