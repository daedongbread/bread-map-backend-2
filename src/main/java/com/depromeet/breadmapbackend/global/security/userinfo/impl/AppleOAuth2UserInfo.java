package com.depromeet.breadmapbackend.global.security.userinfo.impl;

import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfo;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getNickName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
