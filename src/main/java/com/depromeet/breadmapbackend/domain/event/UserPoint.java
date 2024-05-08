package com.depromeet.breadmapbackend.domain.event;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseEntity {

    @Id
    private long userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotNull
    private int totalPoint = 0;

    UserPoint(long userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    void addPoint(int point) {
        totalPoint += point;
    }
}
