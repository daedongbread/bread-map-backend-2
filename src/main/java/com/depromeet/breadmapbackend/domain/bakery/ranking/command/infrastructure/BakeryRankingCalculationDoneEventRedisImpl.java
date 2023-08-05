package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryRankingCalculationDoneEvent;
import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingCalculationDoneEventRedisImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankingCalculationDoneEventRedisImpl implements BakeryRankingCalculationDoneEvent {

	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final LocalDate calculatedDate) {

		log.info("========================= Publish Bakery Ranking Calculation Done  Event =========================");
		final EventInfo calculateRankingDoneEvent = EventInfo.BAKERY_RANKING_CALCULATION_DONE_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		final String calculateDate = calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		fieldMap.put(calculateRankingDoneEvent.getEvenMessageKeys().get(0), calculateDate);

		final String eventName = calculateRankingDoneEvent.getEventName();
		redisTemplate.opsForStream().add(eventName, fieldMap);
	}
}
