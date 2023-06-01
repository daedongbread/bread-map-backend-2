package com.depromeet.breadmapbackend.global.security.token;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OICDUserInfo;

@Component
public class OIDCVerifyProcessorImpl implements OIDCVerifyProcessor {

	private final List<OIDCVerifierTemplate> oidcVerifierList;

	public OIDCVerifyProcessorImpl(final List<OIDCVerifierTemplate> oidcVerifierList) {
		this.oidcVerifierList = oidcVerifierList;
	}

	@Override
	public OICDUserInfo verifyIdToken(final OAuthType oAuthType, final String idToken) {
		OIDCVerifierTemplate tokenVerifier = routingVerifierCaller(oAuthType);
		return tokenVerifier.verifyIdToken(idToken);
	}

	private OIDCVerifierTemplate routingVerifierCaller(final OAuthType oAuthType) {
		return oidcVerifierList.stream()
			.filter(oidcVerifier -> oidcVerifier.support(oAuthType))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Provider 타입입니다."));
	}

}
