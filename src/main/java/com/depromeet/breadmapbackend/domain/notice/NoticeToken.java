package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeToken extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String deviceToken;

    @Column(nullable = false)
    private boolean isAlarmOn;

    @Builder
    public NoticeToken(User user, String deviceToken, boolean isAlarmOn) {
        this.user = user;
        this.deviceToken = deviceToken;
        this.isAlarmOn = true;
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updateAlarm() { this.isAlarmOn = !this.isAlarmOn; }
}
