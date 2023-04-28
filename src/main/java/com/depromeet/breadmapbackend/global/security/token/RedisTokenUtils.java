package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
        return !redisTemplate.hasKey(customRedisProperties.getKey().getDelete() + ":" + oAuthId);
    }

    public void setRefreshToken(String refreshToken, String value, Long expiredDate) {
        redisTemplate.opsForValue().set(refreshToken, value, expiredDate, TimeUnit.MILLISECONDS);
    }

    public Boolean isRefreshTokenValid(String refreshToken, String accessToken) {
        if (redisTemplate.opsForValue().get(refreshToken) != null)
            return accessToken.equals(redisTemplate.opsForValue().get(refreshToken).split(":")[1]);
        else return Boolean.FALSE;
    }

    public String getOAuthIdFromRefreshToken(String refreshToken) {
        if (redisTemplate.opsForValue().get(refreshToken) != null)
            return redisTemplate.opsForValue().get(refreshToken).split(":")[0];
        else return "";
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
}
