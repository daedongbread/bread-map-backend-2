package com.depromeet.breadmapbackend.domain.event.db;

import com.depromeet.breadmapbackend.domain.event.domain.point.PointHistoryType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class UserPointHistoryEntity extends TimestampEntity {
    @Id @GeneratedValue
    private long id;
    @NotNull
    private long userId;
    @NotNull
    private int point;
    @NotNull
    private int grandTotalPoint;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PointHistoryType type = PointHistoryType.ETC;
    @NotNull
    private String description = "";

    UserPointHistoryEntity(long id, long userId, int point, int grandTotalPoint, String description) {
        this.id = id;
        this.userId = userId;
        this.point = point;
        this.grandTotalPoint = grandTotalPoint;
        this.description = description;
    }
}
