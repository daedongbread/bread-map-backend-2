package com.depromeet.breadmapbackend.global.security.token;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OIDCUserInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OIDCVerifyProcessorImpl implements OIDCVerifyProcessor {

	private final List<OIDCVerifierTemplate> oidcVerifierList;

	@Override
	public OIDCUserInfo verifyIdToken(final OAuthType oAuthType, final String idToken) {
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
