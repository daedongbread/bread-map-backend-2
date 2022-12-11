package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.review.RecommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLikeEvent;
import com.depromeet.breadmapbackend.domain.review.ReviewLikeEvent;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import org.springframework.data.domain.Pageable;

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
    PageResponseDto<NoticeDto> getTodayNoticeList(String username, Pageable pageable);
    PageResponseDto<NoticeDto> getWeekNoticeList(String username, Pageable pageable);
    PageResponseDto<NoticeDto> getBeforeNoticeList(String username, Pageable pageable);
}
