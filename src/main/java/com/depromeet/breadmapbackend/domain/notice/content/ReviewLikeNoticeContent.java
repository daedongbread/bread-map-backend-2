package com.depromeet.breadmapbackend.domain.notice.content;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewLikeNoticeContent implements NoticeContent {

	private static final String NOTICE_TITLE_FORMAT = "내 리뷰를 %s님이 좋아해요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.REVIEW_LIKE;
	private final CustomAWSS3Properties customAwss3Properties;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getLike()
			+ ".png";
	}

	@Override
	public String getTitle(final Notice notice) {
		return NOTICE_TITLE_FORMAT.formatted(getNickName(notice));
	}
}
