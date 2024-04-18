package com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent;

import lombok.Getter;

@Getter
public class BreadDiaryEventContent {
    private BreadDiaryEventState state = BreadDiaryEventState.PENDING;
    private String description = "";
    private int point = 500;

    public String getIncreaseKeyword() {
        return point < 0 ? "차감" : "지급";
    }
}
