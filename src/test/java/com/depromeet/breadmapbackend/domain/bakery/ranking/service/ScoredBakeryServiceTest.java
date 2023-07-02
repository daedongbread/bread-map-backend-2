package com.depromeet.breadmapbackend.domain.bakery.ranking.service;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryRepositoryImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryServiceImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * ScoredBakeryServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@Import({
	ScoredBakeryServiceImpl.class,
	TestConfig.class,
	AsyncConfig.class,
	ScoredBakeryRepositoryImpl.class
})
public abstract class ScoredBakeryServiceTest extends ServiceTest {
}
