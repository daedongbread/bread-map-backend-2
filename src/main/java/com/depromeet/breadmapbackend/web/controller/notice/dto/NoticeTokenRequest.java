package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeTokenRequest {
    private String deviceToken;

    @Builder
    public NoticeTokenRequest(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
