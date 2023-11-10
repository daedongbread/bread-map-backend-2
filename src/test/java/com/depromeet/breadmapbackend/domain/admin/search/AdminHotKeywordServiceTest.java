package com.depromeet.breadmapbackend.domain.admin.search;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * AdminHotKeywordServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */

@Import({
	AdminHotKeywordServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
})
public abstract class AdminHotKeywordServiceTest extends ServiceTest {
}
