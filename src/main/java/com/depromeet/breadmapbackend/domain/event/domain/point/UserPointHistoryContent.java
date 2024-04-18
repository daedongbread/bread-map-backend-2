package com.depromeet.breadmapbackend.domain.event.domain.point;

import lombok.Getter;

@Getter
public class UserPointHistoryContent {
    private final int point;
    private final int grandTotalPoint;
    private final PointHistoryType type;
    private final String description;

    public UserPointHistoryContent(int point, int grandTotalPoint, PointHistoryType type, String description) {
        this.point = point;
        this.grandTotalPoint = grandTotalPoint;
        this.type = type;
        this.description = description;
    }


}
