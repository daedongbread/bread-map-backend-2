package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.review.comment.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.follow.FollowEvent;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface NoticeService {
    void addNoticeToken(String username, NoticeTokenRequest request);
    void deleteNoticeToken(NoticeTokenDeleteEvent event);
    void addFollowNotice(FollowEvent event) throws FirebaseMessagingException;
    void addReviewCommentNotice(ReviewCommentEvent event) throws FirebaseMessagingException;
    void addReviewLikeNotice(ReviewLikeEvent event) throws FirebaseMessagingException;
    void addRecommentNotice(RecommentEvent event) throws FirebaseMessagingException;
    void addReviewCommentLikeNotice(ReviewCommentLikeEvent event) throws FirebaseMessagingException;
    PageResponseDto<NoticeDto> getNoticeList(String username, NoticeDayType type, Long lastId, int page);
}
