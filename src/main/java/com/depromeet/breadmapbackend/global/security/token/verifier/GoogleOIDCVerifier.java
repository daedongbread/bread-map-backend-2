package com.depromeet.breadmapbackend.global.security.token.verifier;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.client.GoogleOAuthClient;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.OIDCVerifierTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleOIDCVerifier extends OIDCVerifierTemplate {

	private static final String ISSUER = "https://accounts.google.com";
	private static final OAuthType SUPPORT_TYPE = OAuthType.GOOGLE;
	private final GoogleOAuthClient googleOAuthClient;

	@Override
	protected boolean support(final OAuthType oAuthType) {
		return SUPPORT_TYPE == oAuthType;
	}

	@Override
	protected OIDCPublicKeysDto getOIDCPublicKeysDto(final String iss) {
		if (ISSUER.equals(iss))
			return getAppleOIDCPublicKeys();
		else
			throw new DaedongException(DaedongStatus.OIDC_ISSUER_WRONG);
	}

	@Override
	protected OAuthType getOAuthType() {
		return SUPPORT_TYPE;
	}

	@Cacheable(cacheNames = "GoogleOIDC", cacheManager = "oidcCacheManager")
	public OIDCPublicKeysDto getAppleOIDCPublicKeys() {
		return googleOAuthClient.getOIDCPublicKeys();
	}

}
