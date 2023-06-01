package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OICDUserInfo;

public interface OIDCVerifyProcessor {

	OICDUserInfo verifyIdToken(final OAuthType oAuthType, final String idToken);
}
