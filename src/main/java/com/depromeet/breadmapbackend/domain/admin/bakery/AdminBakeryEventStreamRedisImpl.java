package com.depromeet.breadmapbackend.domain.admin.bakery;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.DaeDongEvents;

import lombok.RequiredArgsConstructor;

/**
 * AdminBakeryEventStreamRedisImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */
@Component
@RequiredArgsConstructor
public class AdminBakeryEventStreamRedisImpl implements AdminBakeryEventStream {

	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final DaeDongEvents event, final Map<String, String> message) {
		redisTemplate.opsForStream()
			.add(event.name(), message);
	}
}
