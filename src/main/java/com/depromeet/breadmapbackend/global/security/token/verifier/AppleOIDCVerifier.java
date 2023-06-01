package com.depromeet.breadmapbackend.global.security.token.verifier;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.client.AppleOAuthClient;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.OIDCVerifierTemplate;

@Component
public class AppleOIDCVerifier extends OIDCVerifierTemplate {

	private static final String ISSUER = "https://appleid.apple.com";
	private static final OAuthType SUPPORT_TYPE = OAuthType.APPLE;
	private final AppleOAuthClient appleOAuthClient;

	public AppleOIDCVerifier(final AppleOAuthClient appleOAuthClient) {
		this.appleOAuthClient = appleOAuthClient;
	}

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

	@Cacheable(cacheNames = "AppleOICD", cacheManager = "oidcCacheManager")
	public OIDCPublicKeysDto getAppleOIDCPublicKeys() {
		return appleOAuthClient.getOIDCPublicKeys();
	}

}
