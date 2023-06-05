package com.depromeet.breadmapbackend.domain.notice.content;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlagBakeryChangeNoticeContent implements NoticeContent {
	private static final NoticeType SUPPORT_TYPE = NoticeType.FLAG_BAKERY_CHANGE;
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
		return "";
	}
}
