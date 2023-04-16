package com.depromeet.breadmapbackend.global.security.userinfo;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.impl.*;
import io.jsonwebtoken.Claims;

public class OIDCUserInfoFactory {
    public static OIDCUserInfo getOIDCUserInfo(OAuthType OAuthType, Claims body) {
        switch (OAuthType) {
            case GOOGLE: return new GoogleOIDCUserInfo(body);
            case APPLE: return new AppleOIDCUserInfo(body);
            case KAKAO: return new KakaoOIDCUserInfo(body);
            default: throw new DaedongException(DaedongStatus.OIDC_ISSUER_WRONG);
        }
    }
}
