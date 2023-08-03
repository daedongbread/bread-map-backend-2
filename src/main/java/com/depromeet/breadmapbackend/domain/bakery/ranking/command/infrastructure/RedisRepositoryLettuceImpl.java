package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.RedisRepository;

import lombok.RequiredArgsConstructor;

/**
 * RedisRepositoryLettuceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
@Repository
@RequiredArgsConstructor
public class RedisRepositoryLettuceImpl implements RedisRepository {

	private final StringRedisTemplate redisTemplate;

	@Override
	public Optional<Long> increment(final String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().increment(key));
	}
}
