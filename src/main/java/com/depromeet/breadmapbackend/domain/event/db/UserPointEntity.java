package com.depromeet.breadmapbackend.domain.event.db;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Table(name = "user_point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class UserPointEntity extends TimestampEntity {
    @Id
    @GeneratedValue
    private long userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private User user;

    @NotNull
    private int totalPoint = 0;

    UserPointEntity(long userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    void addPoint(int point) {
        totalPoint += point;
    }
}
