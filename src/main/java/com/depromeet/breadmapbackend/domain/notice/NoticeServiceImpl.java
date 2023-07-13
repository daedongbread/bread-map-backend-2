package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto.*;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
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
	private final FollowRepository followRepository;
	private final FcmService fcmService;
	private final NoticeContentProcessor noticeContentProcessor;

	@Async("notice")
	@TransactionalEventListener
	@Transactional()
	public void addNotice(final NoticeEvent noticeEvent) {
		final Notice savedNotice = noticeRepository.save(noticeEvent.toNotice());

		if (noticeEvent.getIsAlarmOn()) {
			try {
				fcmService.sendMessageTo(generateNoticeDtoForFcm(savedNotice));
			} catch (FirebaseMessagingException e) {
				// TODO : 예외 처리 FirebaseMessagingException
				throw new RuntimeException(e);
			}
		}
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
			.image(noticeContentProcessor.getImage(notice))
			.title(noticeContentProcessor.getTitle(notice))
			.isFollow(followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent())
			.notice(notice)
			.build();
	}

	private NoticeFcmDto generateNoticeDtoForFcm(final Notice notice) {
		return NoticeFcmDto.builder()
			.userId(notice.getUser().getId())
			.title(noticeContentProcessor.getTitle(notice))
			.content(notice.getContent())
			.contentId(notice.getContentId())
			.type(notice.getType())
			.build();
	}

}
