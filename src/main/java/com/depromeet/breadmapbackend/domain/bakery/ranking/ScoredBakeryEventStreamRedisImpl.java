package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScoredBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScoredBakeryEventStreamRedisImpl implements ScoredBakeryEventStream {

	private final StringRedisTemplate redisTemplate;

	public void publishCalculateRankingEvent(final LocalDate calculatedDate) {
		log.info("========================= Publish Calculating Bakery Ranking Event =========================");
		final EventInfo calculateRankingEvent = EventInfo.CALCULATE_RANKING_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		final String calculateDate = calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		fieldMap.put(calculateRankingEvent.getEvenMessageKeys().get(0), calculateDate);

		final String eventName = calculateRankingEvent.getEventName();

		final String EVENT_KEY = eventName + ":" + calculateDate;

		log.info("EVENT_KEY: {}", EVENT_KEY);
		if (isCalculationStarted(EVENT_KEY)) {
			log.info("Calculation is not started yet. Start calculating ranking");
			redisTemplate.opsForStream().add(eventName, fieldMap);
		} else {
			log.info("Calculation is already started. Skip calculating ranking");
		}
	}

	private boolean isCalculationStarted(final String EVENT_KEY) {
		final Optional<Long> incrementedValue = Optional.ofNullable(
			redisTemplate.opsForValue().increment(EVENT_KEY)
		);
		return incrementedValue.isPresent() && incrementedValue.get() == 1L;
	}

}
