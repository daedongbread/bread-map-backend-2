package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingScheduler
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/17
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankingScheduler {

	private final StringRedisTemplate redisTemplate;

	@Scheduled(cron = "0 0 0 * * *")
	public void publishBakeryRankingCalculationEvent() {
		log.info("========================= Start Calculating Bakery Ranking =========================");
		final EventInfo calculateRankingEvent = EventInfo.CALCULATE_RANKING_EVENT;
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put(calculateRankingEvent.getEvenMessageKeys().get(0),
			LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		redisTemplate.opsForStream()
			.add(calculateRankingEvent.name(), fieldMap);

	}
}
