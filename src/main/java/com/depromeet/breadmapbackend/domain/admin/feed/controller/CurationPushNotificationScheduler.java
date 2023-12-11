package com.depromeet.breadmapbackend.domain.admin.feed.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.CurationFeedRepository;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CurationPushNotificationScheduler
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/10/12
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CurationPushNotificationScheduler {

	private static final String NOTIFICATION_SEND_KEY = "notification";
	private final StringRedisTemplate redisTemplate;
	private final ApplicationEventPublisher eventPublisher;
	private final CurationFeedRepository curationFeedRepository;

	@Scheduled(cron = "0 0 14 * * *")
	@Transactional
	public void publishCurationPushNotificationEvent() {
		log.info("========================= Send Curation Notification =========================");
		if (isNotificationSent()) {
			final LocalDateTime now = LocalDateTime.now();
			final List<CurationFeed> curationList =
				curationFeedRepository.findByActivatedAndActiveTimeBetween(
					FeedStatus.POSTING,
					LocalDateTime.of(
						now.getYear(),
						now.getMonthValue(),
						now.getDayOfMonth() - 1,
						14, 0),
					LocalDateTime.of(
						now.getYear(),
						now.getMonthValue(),
						now.getDayOfMonth(),
						14, 0)
				);

			curationList.forEach(
				curation -> eventPublisher.publishEvent(
					NoticeEventDto.builder()
						.contentId(curation.getId())
						.noticeType(NoticeType.CURATION)
						.build()
				)
			);
		}
	}

	private boolean isNotificationSent() {
		final String calculateDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		final String lockKey = NOTIFICATION_SEND_KEY + ":" + calculateDate;

		final Optional<Long> incrementedValue = Optional.ofNullable(
			redisTemplate.opsForValue().increment(lockKey)
		);
		return incrementedValue.isPresent() && incrementedValue.get() == 1L;
	}
}
