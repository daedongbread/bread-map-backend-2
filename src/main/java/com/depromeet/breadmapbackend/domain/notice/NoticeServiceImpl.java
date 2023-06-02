package com.depromeet.breadmapbackend.domain.notice;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddEvent;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.review.comment.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowEvent;
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
	private final NoticeGenerateFactory noticeGenerateFactory;
	private final FcmService fcmService;

	@Async("notice")
	@TransactionalEventListener
	public void addFollowNotice(FollowEvent event) throws FirebaseMessagingException { // 팔로우 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(event.getFromUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(fromUser)
			.contentId(fromUser.getId())
			.type(NoticeType.FOLLOW).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
		);
	}

	@Async("notice")
	@TransactionalEventListener
	public void addReviewCommentNotice(ReviewCommentEvent event) throws FirebaseMessagingException { // 내 리뷰 댓글 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(event.getFromUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(fromUser)
			// contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
			.contentId(event.getReviewId()).content(event.getReviewContent())
			.type(NoticeType.REVIEW_COMMENT).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
		);
	}

	@Async("notice")
	@TransactionalEventListener
	public void addReviewLikeNotice(ReviewLikeEvent event) throws FirebaseMessagingException { // 내 리뷰 좋아요 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(event.getFromUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(fromUser)
			// contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
			.contentId(event.getReviewId()).content(event.getReviewContent())
			.type(NoticeType.REVIEW_LIKE).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
		);
	}

	@Async("notice")
	@TransactionalEventListener
	public void addRecommentNotice(RecommentEvent event) throws FirebaseMessagingException { // 내 대댓글 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(event.getFromUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(fromUser)
			// contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
			.contentId(event.getCommentId()).content(event.getCommentContent())
			.type(NoticeType.RECOMMENT).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
		);
	}

	@Async("notice")
	@TransactionalEventListener
	public void addReviewCommentLikeNotice(ReviewCommentLikeEvent event) throws
		FirebaseMessagingException { // 내 댓글 좋아요 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(event.getFromUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(fromUser)
			// contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
			.contentId(event.getCommentId()).content(event.getCommentContent())
			.type(NoticeType.REVIEW_COMMENT_LIKE).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
		);
	}

	@Async("notice")
	@TransactionalEventListener
	public void addReportBakeryAddNotice(BakeryAddEvent event) throws FirebaseMessagingException { // 제보한 빵집 추가 알람
		User user = userRepository.findById(event.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Notice notice = Notice.builder()
			.user(user).fromUser(user)
			// contentId : 추가된 빵집 아이디, content : 추가된 빵집 이름
			.contentId(event.getBakeryId()).content(event.getBakeryName())
			.type(NoticeType.ADD_BAKERY).build();
		noticeRepository.save(notice);
		fcmService.sendMessageTo(
			user,
			noticeGenerateFactory.getTitle(notice),
			notice.getContent(),
			notice.getContentId(),
			notice.getType()
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
				.map(notice -> generateNoticeDtoFromFactory(user, notice))
				.collect(Collectors.toList())
		);
	}

	private NoticeDto generateNoticeDtoFromFactory(final User user, final Notice notice) {
		return noticeGenerateFactory.generateNotice(
			notice,
			followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent()
		);
	}

}
