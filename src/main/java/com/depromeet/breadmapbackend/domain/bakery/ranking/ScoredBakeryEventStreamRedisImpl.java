package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

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

	public void publish(final EventInfo event, final LocalDate calculatedDate) {
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put("calculatedDate", calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		redisTemplate.opsForStream()
			.add(event.name(), fieldMap);
	}
}
