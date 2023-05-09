package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OAuthInfo oAuthInfo;

    @Embedded
    private UserInfo userInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private final RoleType roleType = RoleType.USER;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isBlock = Boolean.FALSE;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isMarketingInfoReceptionAgreed;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isAlarmOn = Boolean.FALSE;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flag> flagList = new ArrayList<>();

    public String getOAuthId() {
        return this.oAuthInfo.getOAuthId();
    }

    public String getNickName() {
        return this.userInfo.getNickName();
    }

    public void changeBlock() {
        this.isBlock = !this.isBlock;
    }

    public boolean alarmOn() {
        this.isAlarmOn = true;
        return true;
    }

    public boolean alarmOff() {
        this.isAlarmOn = false;
        return false;
    }
}
