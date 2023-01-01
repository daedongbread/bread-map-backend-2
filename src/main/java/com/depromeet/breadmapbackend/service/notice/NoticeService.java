package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.review.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    void addNoticeToken(String username, NoticeTokenRequest request);
    void deleteNoticeToken(NoticeTokenDeleteEvent event);
    void addFollowNotice(FollowEvent event) throws FirebaseMessagingException;
    void addReviewCommentNotice(ReviewCommentEvent event) throws FirebaseMessagingException;
    void addReviewLikeNotice(ReviewLikeEvent event) throws FirebaseMessagingException;
    void addRecommentNotice(RecommentEvent event) throws FirebaseMessagingException;
    void addReviewCommentLikeNotice(ReviewCommentLikeEvent event) throws FirebaseMessagingException;
    PageResponseDto<NoticeDto> getTodayNoticeList(String username, Pageable pageable);
    PageResponseDto<NoticeDto> getWeekNoticeList(String username, Pageable pageable);
    PageResponseDto<NoticeDto> getBeforeNoticeList(String username, Pageable pageable);
}
