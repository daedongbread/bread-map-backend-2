package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
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
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.UpdateNoticeTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private String commentImage = "noticeImage/comment.jpg";
    private String likeImage = "noticeImage/like.jpg";
    private String reportImage = "noticeImage/report.jpg";
    private String flagImage = "noticeImage/flag.jpg";

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeToken(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent())
            throw new NoticeTokenAlreadyException();
        NoticeToken noticeToken = NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build();
        noticeTokenRepository.save(noticeToken);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeToken(String username, UpdateNoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getOldDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, request.getOldDeviceToken()).get();
            noticeToken.updateDeviceToken(request.getNewDeviceToken());
        } else throw new NoticeTokenNotFoundException();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNoticeToken(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get();
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
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title(fromUser.getNickName() + "님이 회원님을 팔로우하기 시작했어요")
                .type(NoticeType.FOLLOW).build();
        noticeRepository.save(notice);
    }

    @Async
    @TransactionalEventListener
    public void addReviewCommentNotice(ReviewCommentEvent event) { // 내 리뷰 댓글 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(UserNotFoundException::new);
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 리뷰에 " + fromUser.getNickName() + "님이 댓글을 달았어요!")
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰
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
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰
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
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글
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
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 제목
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
    public List<NoticeDto> getTodayNoticeList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return noticeRepository.findByUser(user).stream()
                .filter(notice -> ChronoUnit.DAYS.between(notice.getCreatedAt(), LocalDateTime.now()) < 1)
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice)).title(notice.getTitle()).fromUserId(notice.getFromUser().getId())
                        .contentId(notice.getContentId()).content(notice.getContent())
                        .createdAt(createAt(notice.getCreatedAt())).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<NoticeDto> getWeekNoticeList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return noticeRepository.findByUser(user).stream()
                .filter(notice -> {
                    long day = ChronoUnit.DAYS.between(notice.getCreatedAt(), LocalDateTime.now());
                    return 1 <= day && day < 7;
                })
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice)).title(notice.getTitle()).fromUserId(notice.getFromUser().getId())
                        .contentId(notice.getContentId()).content(notice.getContent())
                        .createdAt(createAt(notice.getCreatedAt())).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<NoticeDto> getBeforeNoticeList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return noticeRepository.findByUser(user).stream()
                .filter(notice -> 7 <= ChronoUnit.DAYS.between(notice.getCreatedAt(), LocalDateTime.now()))
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice)).title(notice.getTitle()).fromUserId(notice.getFromUser().getId())
                        .contentId(notice.getContentId()).content(notice.getContent())
                        .createdAt(createAt(notice.getCreatedAt())).build())
                .collect(Collectors.toList());
    }

    private String createAt(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.isAfter(now)) throw new NoticeDateException();

        long day = ChronoUnit.DAYS.between(createdAt, now);
        if (day >= 7) return createdAt.getMonthValue() + "월 " + createdAt.getDayOfMonth() + "일";
        else if (day >= 1) return day + "일 전";

        long hour = ChronoUnit.HOURS.between(createdAt, now);
        if (1 <= hour && hour < 24)  return hour + "시간 전";

        long minute = ChronoUnit.MINUTES.between(createdAt, now);
        return minute + "분전";
    }

    private String noticeImage(Notice notice) {
        if(notice.getType().equals(NoticeType.FOLLOW)) return notice.getFromUser().getProfileImageUrl();
        else if(notice.getType().equals(NoticeType.REVIEW_COMMENT) || notice.getType().equals(NoticeType.RECOMMENT))
            return commentImage;
        else if(notice.getType().equals(NoticeType.REVIEW_LIKE) || notice.getType().equals(NoticeType.REVIEW_COMMENT_LIKE))
            return likeImage;
        else if(notice.getType().equals(NoticeType.ADD_BAKERY) || notice.getType().equals(NoticeType.ADD_PRODUCT))
            return reportImage;
        else if(notice.getType().equals(NoticeType.FLAG_BAKERY_CHANGE) || notice.getType().equals(NoticeType.FLAG_BAKERY_ADMIN_NOTICE))
            return flagImage;
        else throw new NoticeTypeWrongException();
    }

}
