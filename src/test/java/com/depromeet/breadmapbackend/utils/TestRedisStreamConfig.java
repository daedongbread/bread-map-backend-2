package com.depromeet.breadmapbackend.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryEventStream;
import com.depromeet.breadmapbackend.domain.bakery.ranking.mock.FakeScoredBakeryEventStreamImpl;

/**
 * TestRedisConfig
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
@Configuration
public class TestRedisStreamConfig {
	@Bean
	@Primary
	public ScoredBakeryEventStream scoredBakeryEventStream() {
		return new FakeScoredBakeryEventStreamImpl();
	}
}
