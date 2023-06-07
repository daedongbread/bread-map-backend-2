package com.depromeet.breadmapbackend.domain.user;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final static Duration USER_CACHE_TTL = Duration.ofDays(2);

	public Boolean setIfAbsent(final User user) {
		try {
			return Boolean.TRUE.equals(
				redisTemplate.opsForValue().setIfAbsent(
					getKey(user.getOAuthId()),
					objectMapper.writeValueAsString(user),
					USER_CACHE_TTL
				)
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Optional<User> getUser(final String oAuthId) {

		try {
			final Optional<String> userInString = Optional.ofNullable(redisTemplate.opsForValue().get(getKey(oAuthId)));
			return userInString.isPresent() ? Optional.of(objectMapper.readValue(userInString.get(), User.class)) :
				Optional.empty();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getKey(String oAuthId) {
		return "USER:" + oAuthId;
	}
}

