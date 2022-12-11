package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeDateException;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTokenAlreadyException;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTokenNotFoundException;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTypeWrongException;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.admin.dto.AdminSimpleBakeryDto;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final NoticeTokenRepository noticeTokenRepository;
    private final FollowRepository followRepository;

    private final FcmService fcmService;

    private String COMMENT_IMAGE = "noticeImage/defaultComment.jpg";
    private String LIKE_IMAGE = "noticeImage/defaultLike.jpg";
    private String REPORT_IMAGE = "noticeImage/defaultReport.jpg";
    private String FLAG_IMAGE = "noticeImage/defaultFlag.jpg";

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeToken(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent())
            throw new NoticeTokenAlreadyException();
        NoticeToken noticeToken = NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build();
        noticeTokenRepository.save(noticeToken);
    }

    @Async
    @TransactionalEventListener
    public void deleteNoticeToken(NoticeTokenDeleteEvent event) {
        User user = userRepository.findByUsername(event.getUsername()).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, event.getDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, event.getDeviceToken()).get();
            noticeTokenRepository.delete(noticeToken);
        } else throw new NoticeTokenNotFoundException();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public NoticeTokenAlarmDto getNoticeTokenAlarm(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get();
            return NoticeTokenAlarmDto.builder().isAlarmOn(noticeToken.isAlarmOn()).build();
        } else throw new NoticeTokenNotFoundException();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeTokenAlarm(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get();
            noticeToken.updateAlarm();
        } else throw new NoticeTokenNotFoundException();
    }

    @Async
    @TransactionalEventListener
    public void addFollowNotice(FollowEvent event) { // 팔로우 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        // TODO : User 정보로 NoticeToken들 가져오기
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title(fromUser.getNickName() + "님이 회원님을 팔로우하기 시작했어요")
                .type(NoticeType.FOLLOW).build();
        noticeRepository.save(notice);
//        fcmService.sendMessageTo(event.g(), ); // TODO : for로 메시지 보내기... 근데 단체로 보내기 있지 않나
    }

    @Async
    @TransactionalEventListener
    public void addReviewCommentNotice(ReviewCommentEvent event) { // 내 리뷰 댓글 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 리뷰에 " + fromUser.getNickName() + "님이 댓글을 달았어요!")
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
                .contentId(event.getReviewId()).content(event.getReviewContent())
                .type(NoticeType.REVIEW_COMMENT).build();
        noticeRepository.save(notice);
    }

    @Async
    @TransactionalEventListener
    public void addReviewLikeNotice(ReviewLikeEvent event) { // 내 리뷰 좋아요 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 리뷰를 " + fromUser.getNickName() + "님이 좋아해요!")
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
                .contentId(event.getReviewId()).content(event.getReviewContent())
                .type(NoticeType.REVIEW_LIKE).build();
        noticeRepository.save(notice);
    }

    @Async
    @TransactionalEventListener
    public void addRecommentNotice(RecommentEvent event) { // 내 대댓글 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 댓글에 " + fromUser.getNickName() + "님이 대댓글을 달았어요!")
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
                .contentId(event.getCommentId()).content(event.getCommentContent())
                .type(NoticeType.RECOMMENT).build();
        noticeRepository.save(notice);
    }

    @Async
    @TransactionalEventListener
    public void addReviewCommentLikeNotice(ReviewCommentLikeEvent event) { // 내 댓글 좋아요 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 댓글을 " + fromUser.getNickName() + "님이 좋아해요!")
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
                .contentId(event.getCommentId()).content(event.getCommentContent())
                .type(NoticeType.REVIEW_COMMENT_LIKE).build();
        noticeRepository.save(notice);
    }

//    @Async
//    @TransactionalEventListener
//    public void addReportBakeryAddNotice(ReportBakeryAdd event) { // 제보한 빵집 추가 알람
//        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
//        Notice notice = Notice.builder()
//                .user(user).title("내가 제보한 빵집이 추가되었어요!")
//                // contentId : 추가된 빵집 아이디, content : 추가된 빵집 이름
//                .contentId(event.getBakeryId()).content(event.getBakeryName())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }
//
//    @Async
//    @TransactionalEventListener
//    public void addReportBreadAddNotice(ReportBreadAddEvent event) { // 제보한 빵 추가 알람
//        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
//        Notice notice = Notice.builder()
//                .user(user).title("내가 제보한 빵이 추가되었어요!")
//                // contentId : 추가된 빵 아이디, content : 추가된 빵집 이름 - 추가된 빵 이름
//                .contentId(event.getBreadId()).content(event.getBakeryName() + " - " + event.getBreadName())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }
//
//    @Async
//    @TransactionalEventListener
//    public void addFlagBakeryAdminNoticeNotice(FlagBakeryAdminEvent event) { // 즐겨찾기 한 빵집 관리자 글 업데이트
//        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
//        Notice notice = Notice.builder()
//                .user(user).title("관리자 글이 업데이트 됐어요!")
//                // contentId : 빵집 관리자 글 아이디, content : 빵집 관리자 글 제목
//                .contentId(event.getArticleId()).content(event.getArticleTitle())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<NoticeDto> getTodayNoticeList(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Page<Notice> all  = noticeRepository.findTop20ByUserAndCreatedAtAfter(
                user, LocalDateTime.now().with(LocalTime.MIN), pageable);

        return PageResponseDto.of(all,
                all.getContent().stream().map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice))
                        .isFollow(followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent())
                        .notice(notice).build())
                        .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<NoticeDto> getWeekNoticeList(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Page<Notice> all  = noticeRepository.findTop20ByUserAndCreatedAtBetween(
                user, LocalDateTime.now().with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                LocalDateTime.now().minusDays(1).with(LocalTime.MAX), pageable);

        return PageResponseDto.of(all,
                all.getContent().stream().map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice))
                        .isFollow(followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent())
                        .notice(notice).build())
                        .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<NoticeDto> getBeforeNoticeList(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Page<Notice> all  = noticeRepository.findTop20ByUserAndCreatedAtBefore(
                user, LocalDateTime.now().with(LocalTime.MAX).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)), pageable);

        return PageResponseDto.of(all,
                all.getContent().stream().map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice))
                        .isFollow(followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent())
                        .notice(notice).build())
                        .collect(Collectors.toList()));
    }

    private String noticeImage(Notice notice) {
        if(notice.getType().equals(NoticeType.FOLLOW)) return notice.getFromUser().getImage();
        else if(notice.getType().equals(NoticeType.REVIEW_COMMENT) || notice.getType().equals(NoticeType.RECOMMENT))
            return COMMENT_IMAGE;
        else if(notice.getType().equals(NoticeType.REVIEW_LIKE) || notice.getType().equals(NoticeType.REVIEW_COMMENT_LIKE))
            return LIKE_IMAGE;
        else if(notice.getType().equals(NoticeType.ADD_BAKERY) || notice.getType().equals(NoticeType.ADD_PRODUCT))
            return REPORT_IMAGE;
        else if(notice.getType().equals(NoticeType.FLAG_BAKERY_CHANGE) || notice.getType().equals(NoticeType.FLAG_BAKERY_ADMIN_NOTICE))
            return FLAG_IMAGE;
        else throw new NoticeTypeWrongException();
    }
}
