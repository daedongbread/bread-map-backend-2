package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryRankingCalculationDoneEvent;

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
	public void publish() {

	}
}
