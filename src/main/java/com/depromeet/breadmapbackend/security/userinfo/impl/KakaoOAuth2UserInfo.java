package com.depromeet.breadmapbackend.security.userinfo.impl;

import com.depromeet.breadmapbackend.security.userinfo.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsername() {
        log.info("username");
        return attributes.get("id").toString();
    }

    @Override
    public String getNickName() {
        log.info("nickName");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        log.info("email");
        return (String) attributes.get("account_email");
    }

    @Override
    public String getImageUrl() {
        log.info("image");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("thumbnail_image");
    }

}
