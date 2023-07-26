package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

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
		final String calculationStartFlag = redisTemplate.opsForValue().get(EVENT_KEY);
		if (!isCalculationStarted(calculationStartFlag)) {
			log.info("Calculation is not started yet. Start calculating ranking");
			redisTemplate.opsForValue().set(EVENT_KEY, "0", Duration.ofSeconds(30L));
			redisTemplate.opsForStream().add(eventName, fieldMap);
		} else {
			log.info("Calculation is already started. Skip calculating ranking");
		}
	}

	private static boolean isCalculationStarted(final String calculationStartFlag) {
		return calculationStartFlag != null && Integer.parseInt(calculationStartFlag) > 0;
	}

}
