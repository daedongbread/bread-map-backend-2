package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddProductNoticeFactory implements NoticeFactory {
	private static final String NOTICE_TITLE_FORMAT = "내가 제보한 빵이 추가되었어요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.ADD_PRODUCT;
	private final CustomAWSS3Properties customAwss3Properties;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return null;
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		return null;
	}

	// @Override
	// public String getImage(final String image) {
	// 	return customAwss3Properties.getCloudFront() + "/" +
	// 		customAwss3Properties.getDefaultImage().getReport()
	// 		+ ".png";
	// }

	// @Override
	// public String getTitle(final String... titleFragment) {
	// 	return NOTICE_TITLE_FORMAT;
	// }

}
