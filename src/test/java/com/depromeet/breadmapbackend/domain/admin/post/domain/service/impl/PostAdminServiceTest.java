package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.admin.post.repository.PostAdminRepositoryImpl;
import com.depromeet.breadmapbackend.domain.notice.FcmService;
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
	PostAdminServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	PostAdminRepositoryImpl.class
})
public abstract class PostAdminServiceTest extends ServiceTest {

}
