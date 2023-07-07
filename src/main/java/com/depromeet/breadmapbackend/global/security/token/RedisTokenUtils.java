package com.depromeet.breadmapbackend.global.security.token;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenUtils {
	private final StringRedisTemplate redisTemplate;
	private final CustomRedisProperties customRedisProperties;
	private static final String INVALID = "invalid";

	public void withDrawal(String oAuthId) {
		redisTemplate.opsForValue().set(customRedisProperties.getKey().getDelete() + ":" + oAuthId, oAuthId);
	}

	public Boolean isRejoinPossible(String oAuthId) {
		return Boolean.FALSE.equals(redisTemplate.hasKey(customRedisProperties.getKey().getDelete() + ":" + oAuthId));
	}

	public void setRefreshToken(String refreshToken, String value, Long expiredDate) {
		redisTemplate.opsForValue().set(refreshToken, value, expiredDate, TimeUnit.MILLISECONDS);
	}

	public Boolean isRefreshTokenValid(String refreshToken, String accessToken) {
		final Optional<String> accessTokenWithOAuthId = getAccessTokenWithOAuthId(refreshToken);

		return accessTokenWithOAuthId.isPresent() && accessToken.equals(accessTokenWithOAuthId.get().split(":")[1]);
	}

	public String getOAuthIdFromRefreshToken(String refreshToken) {
		final Optional<String> accessTokenWithOAuthId = getAccessTokenWithOAuthId(refreshToken);

		return accessTokenWithOAuthId.isPresent() ? accessTokenWithOAuthId.get().split(":")[0] : "";
	}

	public void setAccessTokenBlackList(String accessToken, Long expirationDate) {
		redisTemplate.opsForValue().set(accessToken, INVALID, expirationDate, TimeUnit.MILLISECONDS);
	}

	public void deleteRefreshToken(String refreshToken) {
		redisTemplate.delete(refreshToken);
	}

	public Boolean isBlackList(String accessToken) {
		return (INVALID.equals(redisTemplate.opsForValue().get(accessToken))) ? Boolean.TRUE : Boolean.FALSE;
	}

	private Optional<String> getAccessTokenWithOAuthId(final String refreshToken) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(refreshToken));
	}

}
