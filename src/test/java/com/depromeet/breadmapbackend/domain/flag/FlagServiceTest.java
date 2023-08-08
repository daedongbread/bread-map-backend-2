package com.depromeet.breadmapbackend.domain.flag;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.domain.post.comment.CommentQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewServiceImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

@Import({
	FlagServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	ReviewServiceImpl.class,
	ReviewQueryRepository.class,
	CommentQueryRepository.class
})
public abstract class FlagServiceTest extends ServiceTest {
}
