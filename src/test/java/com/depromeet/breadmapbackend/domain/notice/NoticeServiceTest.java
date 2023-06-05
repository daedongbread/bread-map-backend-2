package com.depromeet.breadmapbackend.domain.notice;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.content.AddBakeryNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.AddProductNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.FlagBakeryAdminNoticeNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.FlagBakeryChangeNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.FollowNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.RecommentContent;
import com.depromeet.breadmapbackend.domain.notice.content.ReviewCommentLikeNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.ReviewCommentNoticeContent;
import com.depromeet.breadmapbackend.domain.notice.content.ReviewLikeNoticeContent;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomFirebaseProperties;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

@Import({
	NoticeServiceImpl.class,
	NoticeQueryRepository.class,
	FcmService.class,
	NoticeContentProcessorImpl.class,
	TestConfig.class,
	AsyncConfig.class,
	AddBakeryNoticeContent.class,
	AddProductNoticeContent.class,
	FlagBakeryAdminNoticeNoticeContent.class,
	FlagBakeryChangeNoticeContent.class,
	FollowNoticeContent.class,
	RecommentContent.class,
	ReviewCommentLikeNoticeContent.class,
	ReviewCommentNoticeContent.class,
	ReviewLikeNoticeContent.class
})
@EnableConfigurationProperties({
	CustomFirebaseProperties.class,
	CustomAWSS3Properties.class
})
public abstract class NoticeServiceTest extends ServiceTest {
}
