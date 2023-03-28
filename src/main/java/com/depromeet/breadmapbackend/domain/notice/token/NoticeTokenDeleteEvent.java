package com.depromeet.breadmapbackend.domain.notice.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NoticeTokenDeleteEvent {
    private String username;
    private String deviceToken;
}
