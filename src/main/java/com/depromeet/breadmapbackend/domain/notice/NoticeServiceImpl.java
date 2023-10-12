package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.depromeet.breadmapbackend.domain.notice.dto.BasicNoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	private final NoticeRepository noticeRepository;
	private final NoticeQueryRepository noticeQueryRepository;
	private final UserRepository userRepository;
	private final FcmService fcmService;
	private final NoticeFactoryProcessor noticeFactoryProcessor;

	@Async("notice")
	@TransactionalEventListener
	@Transactional()
	public void sendPushNotice(final NoticeEventDto noticeEventDto) {

		final List<Notice> savedNotices = noticeRepository.saveAll(
			noticeFactoryProcessor.createNotice(noticeEventDto)
		);
		final List<String> deviceTokens = savedNotices.stream()
			.filter(notice -> notice.getUser().getIsAlarmOn() && !notice.getUser().getNoticeTokens().isEmpty())
			.flatMap(notice -> notice.getUser().getNoticeTokens().stream().map(NoticeToken::getDeviceToken))
			.toList();

		try {
			fcmService.sendMessageTo(
				generateNoticeDtoForFcm(
					deviceTokens,
					savedNotices.get(0)
				)
			);
		} catch (FirebaseMessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Async("notice")
	@TransactionalEventListener
	@Transactional()
	public void saveNotice(final BasicNoticeEventDto basicNoticeEventDto) {
		final List<Notice> savedNotices = noticeRepository.saveAll(
			noticeFactoryProcessor.createNotice(basicNoticeEventDto)
		);
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public PageResponseDto<NoticeDto> getNoticeList(String oAuthId, NoticeDayType type, Long lastId, int page) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		Page<Notice> content = noticeQueryRepository.findNotice(user, type, lastId, page);
		return PageResponseDto.of(
			content,
			content.getContent()
				.stream()
				.map(notice -> generateNoticeDtoFrom(user, notice))
				.collect(Collectors.toList())
		);
	}

	private NoticeDto generateNoticeDtoFrom(final User user, final Notice notice) {
		return builder()
			.image(noticeFactoryProcessor.getImage(notice))
			.title(notice.getTitle())
			.notice(notice)
			.build();
	}

	private NoticeFcmDto generateNoticeDtoForFcm(
		final List<String> fcmTokens,
		final Notice notice
	) {
		return NoticeFcmDto.builder()
			.fcmTokens(fcmTokens)
			.title(notice.getTitle())
			.content(notice.getContent())
			.contentId(notice.getContentId())
			.type(notice.getType())
			.build();
	}

}
