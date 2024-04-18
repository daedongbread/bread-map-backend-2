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
    @Column(updatable = false)
    private long userId;
    @NotNull
    @Column(updatable = false)
    private int point;
    @NotNull
    @Column(updatable = false)
    private int grandTotalPoint;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(updatable = false)
    private PointHistoryType type = PointHistoryType.ETC;
    @NotNull
    @Column(updatable = false)
    private String description = "";
    @Column(updatable = false)
    private Long targetId;

    UserPointHistoryEntity(long id, long userId, int point, int grandTotalPoint, PointHistoryType type, String description, Long targetId) {
        this.id = id;
        this.userId = userId;
        this.point = point;
        this.grandTotalPoint = grandTotalPoint;
        this.type = type;
        this.description = description;
        this.targetId = targetId;
    }
}
