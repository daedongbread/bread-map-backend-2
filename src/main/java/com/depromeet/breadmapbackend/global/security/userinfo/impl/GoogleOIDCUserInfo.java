package com.depromeet.breadmapbackend.global.security.userinfo.impl;

import com.depromeet.breadmapbackend.global.security.userinfo.OAuth2UserInfo;
import com.depromeet.breadmapbackend.global.security.userinfo.OIDCUserInfo;
import io.jsonwebtoken.Claims;

import java.util.Map;

public class GoogleOIDCUserInfo extends OIDCUserInfo {

    public GoogleOIDCUserInfo(Claims body) {
        super(body);
    }

    @Override
    public String getOAuthId() {
        return (String) body.get("sub");
    }

//    @Override
//    public String getNickName() {
//        return (String) body.get("name");
//    }

    @Override
    public String getEmail() {
        return (String) body.get("email");
    }

//    @Override
//    public String getImage() {
//        return (String) body.get("picture");
//    }
}
