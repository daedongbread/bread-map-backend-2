package com.depromeet.breadmapbackend.domain.notice.content;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlagBakeryAdminNoticeNoticeContent implements NoticeContent {
	private static final String NOTICE_TITLE_FORMAT = "관리자 글이 업데이트 됐어요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.FLAG_BAKERY_ADMIN_NOTICE;
	private final CustomAWSS3Properties customAwss3Properties;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getFlag()
			+ ".png";
	}

	@Override
	public String getTitle(final Notice notice) {
		return NOTICE_TITLE_FORMAT;
	}
}
