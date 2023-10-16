package com.depromeet.breadmapbackend.domain.notice.factory.basic;

import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.dto.BasicNoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BakeryAddBasicNoticeFactory implements BasicNoticeFactory {

	private static final String NOTICE_TITLE_FORMAT = "%s 빵집이 신규 입점했어요!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.BAKERY_ADDED;
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
			customAwss3Properties.getDefaultImage().getReport()
			+ ".png";
	}

	@Override
	public List<Notice> createNotice(final BasicNoticeEventDto basicNoticeEventDto) {

		final List<User> users = userRepository.findByIdNotIn(List.of(basicNoticeEventDto.userId()));
		final Bakery bakery = bakeryRepository.findById(basicNoticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

		return users.stream()
			.map(user ->
				Notice.createNoticeWithContent(
					user,
					NOTICE_TITLE_FORMAT.formatted(bakery.getName()),
					basicNoticeEventDto.contentId(),
					bakery.getName(),
					basicNoticeEventDto.noticeType()
				)
			).toList();
	}
}
