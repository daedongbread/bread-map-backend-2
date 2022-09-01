package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeTokenAlarmDto {
    private boolean alarmOn; // TODO : isAlarmOn 이면 필드 2개

    @Builder
    public NoticeTokenAlarmDto(boolean isAlarmOn) {
        this.alarmOn = isAlarmOn;
    }
}
