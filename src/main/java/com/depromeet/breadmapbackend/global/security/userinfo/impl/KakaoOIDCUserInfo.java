package com.depromeet.breadmapbackend.global.security.userinfo.impl;

import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfo;
import com.depromeet.breadmapbackend.global.security.userinfo.OIDCUserInfo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOIDCUserInfo extends OIDCUserInfo {

    public KakaoOIDCUserInfo(Claims body) {
        super(body);
    }

    @Override
    public String getOAuthId() {
        return body.get("sub").toString();
    }

    @Override
    public String getNickName() {
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) body.get("account_email");
    }

    @Override
    public String getImage() {
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("thumbnail_image");
    }

}
