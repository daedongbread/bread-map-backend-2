package com.depromeet.breadmapbackend.domain.notice.content;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddBakeryNoticeContent implements NoticeContent {

	private static final String NOTICE_TITLE_FORMAT = "내가 제보한 빵집이 추가되었어요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.ADD_BAKERY;
	private final CustomAWSS3Properties customAwss3Properties;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getReport()
			+ ".png";
	}

	@Override
	public String getTitle(final Notice notice) {
		return NOTICE_TITLE_FORMAT;
	}
}
