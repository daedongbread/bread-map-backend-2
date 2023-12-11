package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportBakeryAddNoticeFactory implements NoticeFactory {

	private static final String NOTICE_TITLE_FORMAT = "내가 제보한 빵집이 추가되었어요!";
	// content : 빵집명
	private static final NoticeType SUPPORT_TYPE = NoticeType.REPORT_BAKERY_ADDED;
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final BakeryRepository bakeryRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getBreadAdd()
			+ ".png";
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {

		final User toUser = userRepository.findById(noticeEventDto.userId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		final Bakery bakery = bakeryRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

		return List.of(Notice.createNoticeWithContent(
			toUser,
			NOTICE_TITLE_FORMAT,
			noticeEventDto.contentId(),
			bakery.getName(),
			null,
			noticeEventDto.noticeType()
		));
	}
}
