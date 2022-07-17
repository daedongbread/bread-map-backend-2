package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeDto {
    private String image;
    private String content;
    private String createdAt;

    @Builder
    public NoticeDto(String image, String content, String createdAt) {
        this.image = image;
        this.content = content;
        this.createdAt = createdAt;
    }
}
