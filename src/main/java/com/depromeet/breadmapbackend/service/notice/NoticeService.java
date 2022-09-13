package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.review.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.UpdateNoticeTokenRequest;

import java.util.List;

public interface NoticeService {
    void addNoticeToken(String username, NoticeTokenRequest request);
    void updateNoticeToken(String username, UpdateNoticeTokenRequest request);
    void deleteNoticeToken(String username, NoticeTokenRequest request);
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
