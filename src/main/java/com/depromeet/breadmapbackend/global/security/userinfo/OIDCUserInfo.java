package com.depromeet.breadmapbackend.global.security.userinfo;

import io.jsonwebtoken.Claims;

public abstract class OIDCUserInfo {

    protected Claims body;
    public OIDCUserInfo(Claims body) {
        this.body = body;
    }
    public Claims getBody() {
        return body;
    }
    public abstract String getOAuthId();
    public abstract String getNickName();
    public abstract String getEmail();
    public abstract String getImage();
}
