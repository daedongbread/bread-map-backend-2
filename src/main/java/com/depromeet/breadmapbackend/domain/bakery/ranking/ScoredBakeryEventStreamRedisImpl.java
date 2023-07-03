package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
@Component
@RequiredArgsConstructor
public class ScoredBakeryEventStreamRedisImpl implements ScoredBakeryEventStream {

	private final StringRedisTemplate redisTemplate;

	public void publish(final ScoredBakeryEvents event, final String yearWeekOfMonth) {
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put("yearWeekOfMonth", yearWeekOfMonth);

		redisTemplate.opsForStream()
			.add(event.name(), fieldMap);
	}
}
