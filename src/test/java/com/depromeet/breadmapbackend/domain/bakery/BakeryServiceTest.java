package com.depromeet.breadmapbackend.domain.bakery;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.bakery.sort.DistanceSortProcessor;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewServiceImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

@Import({
	BakeryServiceImpl.class,
	TestConfig.class,
	AsyncConfig.class,
	BakeryQueryRepository.class,
	ReviewServiceImpl.class,
	ReviewQueryRepository.class,
	DistanceSortProcessor.class
})
public abstract class BakeryServiceTest extends ServiceTest {
}

