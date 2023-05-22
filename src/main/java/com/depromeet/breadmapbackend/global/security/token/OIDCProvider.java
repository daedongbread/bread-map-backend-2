package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomOAuthProperties;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.client.AppleOAuthClient;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.client.GoogleOAuthClient;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.client.KakaoOAuthClient;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class OIDCProvider {
    private final GoogleOAuthClient googleOAuthClient;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final AppleOAuthClient appleOAuthClient;
    private final CustomOAuthProperties customOAuthProperties;

    public Claims verifyToken(OAuthType oAuthType, String idToken) {// throws JwkException {
        String[] tokenParts = idToken.split("\\.");
        String kid = JsonPath.read(new String(Base64.getUrlDecoder().decode(tokenParts[0])), "$.kid");
        String iss = JsonPath.read(new String(Base64.getUrlDecoder().decode(tokenParts[1])), "$.iss");
//        String kid = new JSONObject(new String(Base64.getUrlDecoder().decode(tokenParts[0]))).getString("kid");
//        String iss = new JSONObject(new String(Base64.getUrlDecoder().decode(tokenParts[1]))).getString("iss");

        String clientId;
        OIDCPublicKeysDto oidcPublicKeysDto;
        if (iss.equals("https://accounts.google.com") && oAuthType.equals(OAuthType.GOOGLE)) {
            clientId = customOAuthProperties.getGoogle();
            oidcPublicKeysDto = googleOAuthClient.getOIDCPublicKeys();
            log.info("SIZE : " + oidcPublicKeysDto.getKeys().size());
        } else if (iss.equals("https://kauth.kakao.com") && oAuthType.equals(OAuthType.KAKAO)) {
            clientId = customOAuthProperties.getKakao();
            oidcPublicKeysDto = kakaoOAuthClient.getOIDCPublicKeys();
        } else if (iss.equals("https://appleid.apple.com") && oAuthType.equals(OAuthType.APPLE)) {
            clientId = customOAuthProperties.getApple();
            oidcPublicKeysDto = appleOAuthClient.getOIDCPublicKeys();
        } else throw new DaedongException(DaedongStatus.OIDC_ISSUER_WRONG); // TODO
        OIDCPublicKeysDto.OIDCPublicKeyDto oidcPublicKey = getOIDCPublicKey(oidcPublicKeysDto, kid);

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(oidcPublicKey.getN(), oidcPublicKey.getE()))
//                    .requireAudience(clientId) // TODO
                    .build()
                    .parseClaimsJws(idToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION); // TODO
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JwtException e) {
            throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT); // TODO
        }
    }

    private OIDCPublicKeysDto.OIDCPublicKeyDto getOIDCPublicKey(OIDCPublicKeysDto oidcPublicKeysDto, String kid) {
        return oidcPublicKeysDto.getKeys().stream()
                .peek(System.out::println)
                .filter(o -> o.getKid().equals(kid))
                .findFirst()
                .orElseThrow(); // TODO
    }

    private Key getRSAPublicKey(String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }
}
