package com.depromeet.breadmapbackend.domain.event.domain.point;

import lombok.Getter;

@Getter
public class UserPoint {
    private final UserPointPK pk;
    private int totalPoint;

    public void addPoint(int point) {
        totalPoint += point;
    }

    public UserPoint(UserPointPK pk, int totalPoint) {
        this.pk = pk;
        this.totalPoint = totalPoint;
    }
}
