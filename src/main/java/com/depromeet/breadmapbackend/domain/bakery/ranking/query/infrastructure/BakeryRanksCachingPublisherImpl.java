package com.depromeet.breadmapbackend.domain.bakery.ranking.query.infrastructure;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.query.domain.infrastructure.BakeryRanksCachingPublisher;

import lombok.RequiredArgsConstructor;

/**
 * BakeryRanksCachingPublisherImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */

@Component
@RequiredArgsConstructor
public class BakeryRanksCachingPublisherImpl implements BakeryRanksCachingPublisher {

	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish() {

	}
}
