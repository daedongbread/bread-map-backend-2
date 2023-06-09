package com.depromeet.breadmapbackend.domain.user;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;
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

	public Boolean setIfAbsent(final CurrentUserInfo userInfo) {
		try {
			return Boolean.TRUE.equals(
				redisTemplate.opsForValue().setIfAbsent(
					getKey(userInfo.getDelimiterValue()),
					objectMapper.writeValueAsString(userInfo),
					USER_CACHE_TTL
				)
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Optional<CurrentUserInfo> getUser(final String delimiterValue) {

		try {
			final Optional<String> userInString =
				Optional.ofNullable(redisTemplate.opsForValue().get(getKey(delimiterValue)));
			return userInString.isPresent() ?
				Optional.of(objectMapper.readValue(userInString.get(), CurrentUserInfo.class)) :
				Optional.empty();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getKey(String delimiterValue) {
		return "USERINFO:" + delimiterValue;
	}
}

