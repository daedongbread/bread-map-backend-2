package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.BakeryService;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingCalculationService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/28
 */
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BakeryRankingCalculationService {

	private final BakeryService bakeryService;
	private final ScoredBakeryService scoredBakeryService;
	private final StringRedisTemplate redisTemplate;

	public void createBakeryRanking(final String EVENT_KEY, final LocalDate calculateDate) {
		if (isFirstInstanceToCalculateRanks(EVENT_KEY)) {
			log.info("This instance is first instance to calculate ranking");
			calculateRankAndSave(calculateDate);
			log.info("The calculation is done");
		}
	}

	private void calculateRankAndSave(final LocalDate calculateDate) {
		final List<BakeryScoreBaseWithSelectedDate> bakeriesScoreFactors =
			bakeryService.getBakeriesScoreFactors(calculateDate);
		log.info("bakeriesScoreFactors: {}", bakeriesScoreFactors.size());
		scoredBakeryService.calculateBakeryScore(bakeriesScoreFactors);
	}

	private boolean isFirstInstanceToCalculateRanks(final String EVENT_KEY) {
		final Optional<Long> incrementedValue = Optional.ofNullable(
			redisTemplate.opsForValue().increment(EVENT_KEY)
		);
		return incrementedValue.isPresent() && incrementedValue.get() == 2L;
	}

}
