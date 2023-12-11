package com.depromeet.breadmapbackend.domain.post.comment;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.domain.post.PostQueryRepository;
import com.depromeet.breadmapbackend.domain.post.PostRepositoryImpl;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLikeRepositoryImpl;
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
	CommentServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	CommentQueryRepository.class,
	CommentRepositoryImpl.class,
	CommentLikeRepositoryImpl.class,
	PostRepositoryImpl.class,
	PostQueryRepository.class,
})
public abstract class CommentServiceTest extends ServiceTest {

}
