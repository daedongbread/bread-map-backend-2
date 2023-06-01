package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OICDUserInfoTemp;

public interface OIDCVerifyProcessor {

	OICDUserInfoTemp verifyIdToken(final OAuthType oAuthType, final String idToken);
}
