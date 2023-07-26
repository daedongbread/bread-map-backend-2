package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

	private final ScoredBakeryEventStream scoredBakeryEventStream;

	@Scheduled(cron = "0 0 0 * * *")
	public void publishBakeryRankingCalculationEvent() {
		log.info("========================= Start Calculating Bakery Ranking =========================");
		scoredBakeryEventStream.publishCalculateRankingEvent(LocalDate.now());
	}
}
