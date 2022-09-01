package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNoticeTokenRequest {
    private String oldDeviceToken;
    private String newDeviceToken;

    @Builder
    public UpdateNoticeTokenRequest(String oldDeviceToken, String newDeviceToken) {
        this.oldDeviceToken = oldDeviceToken;
        this.newDeviceToken = newDeviceToken;
    }
}
