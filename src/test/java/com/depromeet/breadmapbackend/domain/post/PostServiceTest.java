package com.depromeet.breadmapbackend.domain.post;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.domain.post.like.PostLikeRepositoryImpl;
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
	PostServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	PostQueryRepository.class,
	PostRepositoryImpl.class,
	PostLikeRepositoryImpl.class
})
public abstract class PostServiceTest extends ServiceTest {

}
