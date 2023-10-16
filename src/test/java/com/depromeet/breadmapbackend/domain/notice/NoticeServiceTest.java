package com.depromeet.breadmapbackend.domain.notice;

import org.springframework.context.annotation.Import;

import com.depromeet.breadmapbackend.domain.notice.factory.push.CommentLikeNoticeFactory;
import com.depromeet.breadmapbackend.domain.notice.factory.push.FollowNoticeFactory;
import com.depromeet.breadmapbackend.domain.notice.factory.push.RecommentFactory;
import com.depromeet.breadmapbackend.domain.notice.factory.push.ReportBakeryAddNoticeFactory;
import com.depromeet.breadmapbackend.domain.notice.factory.push.ReviewCommentNoticeFactory;
import com.depromeet.breadmapbackend.domain.notice.factory.push.ReviewLikeNoticeFactory;
import com.depromeet.breadmapbackend.domain.post.comment.CommentQueryRepository;
import com.depromeet.breadmapbackend.domain.post.comment.CommentRepositoryImpl;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

@Import({
	NoticeServiceImpl.class,
	NoticeQueryRepository.class,
	FcmService.class,
	NoticeFactoryProcessorImpl.class,
	TestConfig.class,
	AsyncConfig.class,
	ReportBakeryAddNoticeFactory.class,
	FollowNoticeFactory.class,
	RecommentFactory.class,
	CommentLikeNoticeFactory.class,
	ReviewCommentNoticeFactory.class,
	ReviewLikeNoticeFactory.class,
	CommentRepositoryImpl.class,
	CommentQueryRepository.class
})
public abstract class NoticeServiceTest extends ServiceTest {
}
