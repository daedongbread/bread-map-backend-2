package com.depromeet.breadmapbackend.domain.notice.factory.push;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.CurationFeedRepository;
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
public class CurationNoticeFactory implements NoticeFactory {
	private static final String NOTICE_TITLE_FORMAT = "%s월 %s번째 큐레이션 알림!";
	private static final NoticeType SUPPORT_TYPE = NoticeType.CURATION;
	private final CustomAWSS3Properties customAwss3Properties;
	private final UserRepository userRepository;
	private final CurationFeedRepository curationFeedRepository;

	@Override
	public boolean support(final NoticeType noticeType) {
		return SUPPORT_TYPE == noticeType;
	}

	@Override
	public String getImage(final Notice notice) {
		return customAwss3Properties.getCloudFront() + "/" +
			customAwss3Properties.getDefaultImage().getCuration()
			+ ".png";
	}

	@Override
	public List<Notice> createNotice(final NoticeEventDto noticeEventDto) {
		final List<User> users = userRepository.findUserWithNoticeTokens();
		final LocalDate now = LocalDate.now();
		final CurationFeed curationFeed = curationFeedRepository.findById(noticeEventDto.contentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.CURATION_FEED_NOT_FOUND));

		final long thisMonthCurationOrder =
			curationFeedRepository.countByActivatedAndActiveTimeBetween(
				FeedStatus.POSTING,
				LocalDateTime.of(now.withDayOfMonth(1), LocalTime.of(0, 0)),
				curationFeed.getActiveTime().minusSeconds(1)
			);

		return users.stream().map(
				user -> Notice.createNoticeWithContent(
					user,
					NOTICE_TITLE_FORMAT.formatted(now.getMonthValue(), thisMonthCurationOrder + 1),
					curationFeed.getId(),
					curationFeed.getIntroduction(),
					null,
					SUPPORT_TYPE
				)
			)
			.toList();
	}
}
