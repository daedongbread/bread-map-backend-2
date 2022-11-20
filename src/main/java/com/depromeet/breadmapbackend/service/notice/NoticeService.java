package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.review.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;

import java.util.List;

public interface NoticeService {
    void addNoticeToken(String username, NoticeTokenRequest request);
    void deleteNoticeToken(NoticeTokenDeleteEvent event);
    NoticeTokenAlarmDto getNoticeTokenAlarm(String username, NoticeTokenRequest request);
    void updateNoticeTokenAlarm(String username, NoticeTokenRequest request);
    void addFollowNotice(FollowEvent event);
    void addReviewCommentNotice(ReviewCommentEvent event);
    void addReviewLikeNotice(ReviewLikeEvent event);
    void addRecommentNotice(RecommentEvent event);
    void addReviewCommentLikeNotice(ReviewCommentLikeEvent event);
    List<NoticeDto> getTodayNoticeList(String username);
    List<NoticeDto> getWeekNoticeList(String username);
    List<NoticeDto> getBeforeNoticeList(String username);
}
