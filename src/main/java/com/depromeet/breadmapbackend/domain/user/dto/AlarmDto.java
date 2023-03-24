package com.depromeet.breadmapbackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmDto {
    private boolean alarmOn; // TODO : isAlarmOn 이면 필드 2개

    @Builder
    public AlarmDto(boolean isAlarmOn) {
        this.alarmOn = isAlarmOn;
    }
}
