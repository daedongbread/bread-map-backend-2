package com.depromeet.breadmapbackend.domain.user.follow;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * UserFollowServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/22
 */

@Import({
	UserFollowServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class
})
public abstract class UserFollowServiceTest extends ServiceTest {
}
