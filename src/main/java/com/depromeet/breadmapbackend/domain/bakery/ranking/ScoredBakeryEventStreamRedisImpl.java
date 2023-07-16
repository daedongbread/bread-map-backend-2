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

	public void publishCalculateRankingEvent(final LocalDate calculatedDate) {
		final EventInfo calculateRankingEvent = EventInfo.RE_CALCULATE_RANKING_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put(calculateRankingEvent.getEvenMessageKeys().get(0),
			calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		redisTemplate.opsForStream()
			.add(calculateRankingEvent.name(), fieldMap);
	}

	@Override
	public void publishCachingRankingEvent(final LocalDate calculatedDate) {
		final EventInfo cacheRankingEvent = EventInfo.RE_CACHE_RANKING_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put(cacheRankingEvent.getEvenMessageKeys().get(0),
			calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		redisTemplate.opsForStream()
			.add(cacheRankingEvent.name(), fieldMap);
	}
}
