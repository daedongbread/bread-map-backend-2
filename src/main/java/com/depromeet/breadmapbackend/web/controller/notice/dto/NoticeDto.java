package com.depromeet.breadmapbackend.web.controller.notice.dto;

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

    @Builder
    public NoticeDto(String image, Long fromUserId, String title, Long contentId, String content, LocalDateTime createdAt) {
        this.image = image;
        this.fromUserId = fromUserId;
        this.title = title;
        this.contentId = contentId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
