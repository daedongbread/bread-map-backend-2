package com.depromeet.breadmapbackend.global.security.token;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.userinfo.OICDUserInfoTemp;
import com.jayway.jsonpath.JsonPath;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public abstract class OIDCVerifierTemplate {

	protected abstract boolean support(final OAuthType oAuthType);

	protected final OICDUserInfoTemp verifyIdToken(final String idToken) {
		String[] tokenParts = idToken.split("\\.");
		String kid = JsonPath.read(new String(Base64.getUrlDecoder().decode(tokenParts[0])), "$.kid");
		String iss = JsonPath.read(new String(Base64.getUrlDecoder().decode(tokenParts[1])), "$.iss");

		OIDCPublicKeysDto oidcPublicKeysDto = getOIDCPublicKeysDto(iss);
		OIDCPublicKeysDto.OIDCPublicKeyDto oidcPublicKey = getOIDCPublicKey(oidcPublicKeysDto, kid);

		try {
			final Claims body = Jwts.parserBuilder()
				.setSigningKey(getRSAPublicKey(oidcPublicKey.getN(), oidcPublicKey.getE()))
				.build()
				.parseClaimsJws(idToken)
				.getBody();

			return new OICDUserInfoTemp(
				getOAuthType(),
				body.getSubject(),
				body.get("email", String.class)
			);
		} catch (ExpiredJwtException e) {
			throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | JwtException e) {
			throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
		}
	}

	protected abstract OIDCPublicKeysDto getOIDCPublicKeysDto(final String iss);

	protected abstract OAuthType getOAuthType();

	private OIDCPublicKeysDto.OIDCPublicKeyDto getOIDCPublicKey(final OIDCPublicKeysDto oidcPublicKeysDto,
		final String kid) {
		return oidcPublicKeysDto.getKeys().stream()
			.filter(o -> o.getKid().equals(kid))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.OIDC_PUBLIC_KEY_EXCEPTION));
	}

	private Key getRSAPublicKey(final String modulus, final String exponent) throws
		NoSuchAlgorithmException,
		InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
		byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
		BigInteger n = new BigInteger(1, decodeN);
		BigInteger e = new BigInteger(1, decodeE);

		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
		return keyFactory.generatePublic(keySpec);
	}
}
