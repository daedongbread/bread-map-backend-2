package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
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
	private final FcmService fcmService;
	private final FollowRepository followRepository;
	private final NoticeFactoryProcessor noticeFactoryProcessor;
	private final NoticeTokenRepository noticeTokenRepository;

	@Async("notice")
	@TransactionalEventListener
	@Transactional
	public void sendPushNotice(final NoticeEventDto noticeEventDto) {

		final List<Notice> savedNotices = noticeRepository.saveAll(
			noticeFactoryProcessor.createNotice(noticeEventDto)
		);
		final List<User> alarmOnUserIds = savedNotices.stream()
			.map(Notice::getUser)
			.filter(User::getIsAlarmOn)
			.toList();

		final List<String> deviceTokensToSend = noticeTokenRepository.findByUserIn(alarmOnUserIds)
			.stream().map(NoticeToken::getDeviceToken).distinct().toList();

		try {
			fcmService.sendMessageTo(
				generateNoticeDtoForFcm(
					deviceTokensToSend,
					savedNotices.get(0)
				)
			);
		} catch (FirebaseMessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public PageResponseDto<NoticeDto> getNoticeList(String oAuthId, int page) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		Page<Notice> content = noticeQueryRepository.findNotice(user, page);
		return PageResponseDto.of(
			content,
			content.getContent()
				.stream()
				.map(this::generateNoticeDtoFrom)
				.collect(Collectors.toList())
		);
	}

	private NoticeDto generateNoticeDtoFrom(final Notice notice) {
		return builder()
			.image(noticeFactoryProcessor.getImage(notice))
			.title(notice.getTitle())
			.notice(notice)
			.isFollow(isFollow(notice))
			.build();
	}

	private boolean isFollow(final Notice notice) {
		if (notice.getType() == NoticeType.FOLLOW && notice.getContentId() != null) {
			final User toUser = userRepository.findById(notice.getContentId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
			final Optional<Follow> isFollow = followRepository.findByFromUserAndToUser(notice.getUser(), toUser);
			return isFollow.isPresent();
		}
		return false;
	}

	private NoticeFcmDto generateNoticeDtoForFcm(
		final List<String> fcmTokens,
		final Notice notice
	) {
		return NoticeFcmDto.builder()
			.fcmTokens(fcmTokens)
			.title(notice.getTitle())
			.content(notice.getContentParam() != null && notice.getContent() != null
				? notice.getContent().formatted(notice.getContentParam())
				: notice.getContent())
			.contentId(notice.getContentId())
			.subContentId(notice.getSubContentId())
			.type(notice.getType())
			.build();
	}

}
