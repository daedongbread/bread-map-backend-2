package com.depromeet.breadmapbackend.domain.bakery;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryRepositoryImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryServiceImpl;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewServiceImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * BakeryServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@Import({
	BakeryServiceImpl.class,
	BakeryQueryRepository.class,
	ReviewQueryRepository.class,
	TestConfig.class,
	AsyncConfig.class,
	ReviewServiceImpl.class,
	ScoredBakeryServiceImpl.class,
	ScoredBakeryRepositoryImpl.class
})
public abstract class BakeryServiceTest extends ServiceTest {
}
