package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OIDCUserInfo;

public interface OIDCVerifyProcessor {

	OIDCUserInfo verifyIdToken(final OAuthType oAuthType, final String idToken);
}
