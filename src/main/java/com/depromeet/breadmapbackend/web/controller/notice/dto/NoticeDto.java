package com.depromeet.breadmapbackend.web.controller.notice.dto;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeDto {
    private Long noticeId;
    private String image;
    private Long fromUserId;
    private String fromUserNickName;
    private String title;
    private Long contentId;
    private String content;
    private Boolean isFollow;
    private LocalDateTime createdAt;
    private NoticeType noticeType;

    @Builder
    public NoticeDto(String image, Boolean isFollow, Notice notice) {
        this.noticeId = notice.getId();
        this.image = image;
        this.fromUserId = notice.getFromUser().getId();
        this.fromUserNickName = notice.getFromUser().getNickName();
        this.title = notice.getTitle();
        this.contentId = notice.getContentId();
        this.content = notice.getContent();
        this.isFollow = isFollow;
        this.createdAt = notice.getCreatedAt();
        this.noticeType = notice.getType();
    }
}
