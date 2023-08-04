package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewCreateEvent;
import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankViewCreateEventImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankViewCreateEventImpl implements BakeryRankViewCreateEvent {

	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish() {
		log.info("========================= Publish Calculating Bakery Ranking Event =========================");
		final EventInfo calculateRankingEvent = EventInfo.CALCULATE_BAKERY_RANKING_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		final String calculateDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		fieldMap.put(calculateRankingEvent.getEvenMessageKeys().get(0), calculateDate);

		final String eventName = calculateRankingEvent.getEventName();

		final String EVENT_KEY = eventName + ":" + calculateDate;

		log.info("EVENT_KEY: {}", EVENT_KEY);

		redisTemplate.opsForStream().add(eventName, fieldMap);

	}

}
