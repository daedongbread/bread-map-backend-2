package com.depromeet.breadmapbackend.domain.user;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {

	private final RedisTemplate<String, User> userRedisTemplate;
	private final static Duration USER_CACHE_TTL = Duration.ofDays(2);

	public void setUser(User user) {
		String key = getKey(user.getOAuthId());
		userRedisTemplate.opsForValue().setIfAbsent(key, user, USER_CACHE_TTL);
	}

	public Optional<User> getUser(String userName) {
		String key = getKey(userName);
		User user = userRedisTemplate.opsForValue().get(key);
		return Optional.ofNullable(user);
	}

	private String getKey(String oAuthId) {
		return "USER:" + oAuthId;
	}
}

