package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.impl;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryRankingCalculationEvent;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.BakeryRankingCalculationScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingCalculationSchedulerImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryRankingCalculationSchedulerImpl implements BakeryRankingCalculationScheduler {

	private final BakeryRankingCalculationEvent eventStream;

	@Override
	@Scheduled(cron = "0 0 0 * * *")
	public void command() {
		log.info("========================= Start Calculating Bakery Ranking =========================");
		eventStream.publish(LocalDate.now());
	}
}
