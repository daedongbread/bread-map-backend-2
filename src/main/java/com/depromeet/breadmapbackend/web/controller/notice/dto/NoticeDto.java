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
    private String image;
    private Long fromUserId;
    private String title;
    private Long contentId;
    private String content;
    private LocalDateTime createdAt;
    private NoticeType noticeType;

    @Builder
    public NoticeDto(String image, Notice notice) {
        this.image = image;
        this.fromUserId = notice.getFromUser().getId();
        this.title = notice.getTitle();
        this.contentId = notice.getContentId();
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
        this.noticeType = notice.getType();
    }
}
