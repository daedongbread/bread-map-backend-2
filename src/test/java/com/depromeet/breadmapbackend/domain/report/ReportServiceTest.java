package com.depromeet.breadmapbackend.domain.report;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.domain.post.PostQueryRepository;
import com.depromeet.breadmapbackend.domain.post.PostRepositoryImpl;
import com.depromeet.breadmapbackend.domain.post.comment.CommentQueryRepository;
import com.depromeet.breadmapbackend.domain.post.comment.CommentRepositoryImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * PostServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
@Import({
	ReportServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	ReportRepositoryImpl.class,
	PostRepositoryImpl.class,
	PostQueryRepository.class,
	CommentQueryRepository.class,
	CommentRepositoryImpl.class
})
public abstract class ReportServiceTest extends ServiceTest {

}
