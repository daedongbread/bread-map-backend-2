package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.comment.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.follow.FollowEvent;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeRepository noticeRepository;
    private final NoticeQueryRepository noticeQueryRepository;
    private final UserRepository userRepository;
    private final NoticeTokenRepository noticeTokenRepository;
    private final FollowRepository followRepository;

    private final FcmService fcmService;
    private final CustomAWSS3Properties customAwss3Properties;

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeToken(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent())
            throw new DaedongException(DaedongStatus.NOTICE_TOKEN_DUPLICATE_EXCEPTION);
        NoticeToken noticeToken = NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build();
        noticeTokenRepository.save(noticeToken);
        user.changeAlarm();
    }

    @Async("notice")
    @TransactionalEventListener
    public void deleteNoticeToken(NoticeTokenDeleteEvent event) {
        User user = userRepository.findByUsername(event.getUsername()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(noticeTokenRepository.findByUserAndDeviceToken(user, event.getDeviceToken()).isPresent()) {
            NoticeToken noticeToken = noticeTokenRepository.findByUserAndDeviceToken(user, event.getDeviceToken()).get();
            noticeTokenRepository.delete(noticeToken);
        } else throw new DaedongException(DaedongStatus.NOTICE_TOKEN_NOT_FOUND);
    }

    @Async("notice")
    @TransactionalEventListener
    public void addFollowNotice(FollowEvent event) throws FirebaseMessagingException { // 팔로우 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        // TODO : User 정보로 NoticeToken들 가져오기
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title(fromUser.getNickName() + "님이 회원님을 팔로우하기 시작했어요")
                .contentId(fromUser.getId())
                .type(NoticeType.FOLLOW).build();
        noticeRepository.save(notice);
        fcmService.sendMessageTo(user, notice.getTitle(), notice.getContent(), notice.getContentId(), notice.getType());
    }

    @Async("notice")
    @TransactionalEventListener
    public void addReviewCommentNotice(ReviewCommentEvent event) throws FirebaseMessagingException { // 내 리뷰 댓글 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 리뷰에 " + fromUser.getNickName() + "님이 댓글을 달았어요!")
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
                .contentId(event.getReviewId()).content(event.getReviewContent())
                .type(NoticeType.REVIEW_COMMENT).build();
        noticeRepository.save(notice);
        fcmService.sendMessageTo(user, notice.getTitle(), notice.getContent(), notice.getContentId(), notice.getType());
    }

    @Async("notice")
    @TransactionalEventListener
    public void addReviewLikeNotice(ReviewLikeEvent event) throws FirebaseMessagingException { // 내 리뷰 좋아요 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 리뷰를 " + fromUser.getNickName() + "님이 좋아해요!")
                // contentId : 내가 쓴 리뷰 아이디, content : 내가 쓴 리뷰의 내용(디자인엔 제목으로 나와있음)
                .contentId(event.getReviewId()).content(event.getReviewContent())
                .type(NoticeType.REVIEW_LIKE).build();
        noticeRepository.save(notice);
        fcmService.sendMessageTo(user, notice.getTitle(), notice.getContent(), notice.getContentId(), notice.getType());
    }

    @Async("notice")
    @TransactionalEventListener
    public void addRecommentNotice(RecommentEvent event) throws FirebaseMessagingException { // 내 대댓글 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 댓글에 " + fromUser.getNickName() + "님이 대댓글을 달았어요!")
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
                .contentId(event.getCommentId()).content(event.getCommentContent())
                .type(NoticeType.RECOMMENT).build();
        noticeRepository.save(notice);
        fcmService.sendMessageTo(user, notice.getTitle(), notice.getContent(), notice.getContentId(), notice.getType());
    }

    @Async("notice")
    @TransactionalEventListener
    public void addReviewCommentLikeNotice(ReviewCommentLikeEvent event) throws FirebaseMessagingException { // 내 댓글 좋아요 알람
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(event.getFromUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Notice notice = Notice.builder()
                .user(user).fromUser(fromUser)
                .title("내 댓글을 " + fromUser.getNickName() + "님이 좋아해요!")
                // contentId : 내가 쓴 댓글 아이디, content : 내가 쓴 댓글 내용
                .contentId(event.getCommentId()).content(event.getCommentContent())
                .type(NoticeType.REVIEW_COMMENT_LIKE).build();
        noticeRepository.save(notice);
        fcmService.sendMessageTo(user, notice.getTitle(), notice.getContent(), notice.getContentId(), notice.getType());
    }

//    @Async("notice")
//    @TransactionalEventListener
//    public void addReportBakeryAddNotice(ReportBakeryAdd event) { // 제보한 빵집 추가 알람
//        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
//        Notice notice = Notice.builder()
//                .user(user).title("내가 제보한 빵집이 추가되었어요!")
//                // contentId : 추가된 빵집 아이디, content : 추가된 빵집 이름
//                .contentId(event.getBakeryId()).content(event.getBakeryName())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }
//
//    @Async("notice")
//    @TransactionalEventListener
//    public void addReportBreadAddNotice(ReportBreadAddEvent event) { // 제보한 빵 추가 알람
//        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
//        Notice notice = Notice.builder()
//                .user(user).title("내가 제보한 빵이 추가되었어요!")
//                // contentId : 추가된 빵 아이디, content : 추가된 빵집 이름 - 추가된 빵 이름
//                .contentId(event.getBreadId()).content(event.getBakeryName() + " - " + event.getBreadName())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }
//
//    @Async("notice")
//    @TransactionalEventListener
//    public void addFlagBakeryAdminNoticeNotice(FlagBakeryAdminEvent event) { // 즐겨찾기 한 빵집 관리자 글 업데이트
//        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
//        Notice notice = Notice.builder()
//                .user(user).title("관리자 글이 업데이트 됐어요!")
//                // contentId : 빵집 관리자 글 아이디, content : 빵집 관리자 글 제목
//                .contentId(event.getArticleId()).content(event.getArticleTitle())
//                .type(NoticeType.addBakery).build();
//        noticeRepository.save(notice);
//    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<NoticeDto> getNoticeList(String username, NoticeDayType type, Long lastId, int page) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        Page<Notice> content = noticeQueryRepository.findNotice(user, type, lastId, page);
        return PageResponseDto.of(content,
                content.getContent().stream().map(notice -> NoticeDto.builder()
                        .image(noticeImage(notice))
                        .isFollow(followRepository.findByFromUserAndToUser(notice.getFromUser(), user).isPresent())
                        .notice(notice).build())
                        .collect(Collectors.toList()));
    }

    private String noticeImage(Notice notice) {
        if(notice.getType().equals(NoticeType.FOLLOW)) return notice.getFromUser().getImage();
        else if(notice.getType().equals(NoticeType.REVIEW_COMMENT) || notice.getType().equals(NoticeType.RECOMMENT))
            return customAwss3Properties.getCloudFront() + "/" +
                    customAwss3Properties.getBucket() + "/" +
                    customAwss3Properties.getDefaultImage().getComment() + ".png";
        else if(notice.getType().equals(NoticeType.REVIEW_LIKE) || notice.getType().equals(NoticeType.REVIEW_COMMENT_LIKE))
            return customAwss3Properties.getCloudFront() + "/" +
                    customAwss3Properties.getBucket() + "/" +
                    customAwss3Properties.getDefaultImage().getLike() + ".png";
        else if(notice.getType().equals(NoticeType.ADD_BAKERY) || notice.getType().equals(NoticeType.ADD_PRODUCT))
            return customAwss3Properties.getCloudFront() + "/" +
                    customAwss3Properties.getBucket() + "/" +
                    customAwss3Properties.getDefaultImage().getReport() + ".png";
        else if(notice.getType().equals(NoticeType.FLAG_BAKERY_CHANGE) || notice.getType().equals(NoticeType.FLAG_BAKERY_ADMIN_NOTICE))
            return customAwss3Properties.getCloudFront() + "/" +
                    customAwss3Properties.getBucket() + "/" +
                    customAwss3Properties.getDefaultImage().getFlag() + ".png";
        else throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
    }
}
