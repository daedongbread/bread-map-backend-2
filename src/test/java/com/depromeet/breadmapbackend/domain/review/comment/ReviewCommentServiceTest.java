package com.depromeet.breadmapbackend.domain.review.comment;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

@Import({
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	ReviewCommentServiceImpl.class
})
public abstract class ReviewCommentServiceTest extends ServiceTest {
}
