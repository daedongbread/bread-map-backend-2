package com.depromeet.breadmapbackend.domain.member;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickName;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private String profileImageUrl;

    @Builder
    private Member(String username, String nickName, String email, Gender gender, ProviderType providerType, RoleType roleType, String profileImageUrl) {
        this.username = username;
        this.nickName = nickName;
        this.email = email;
        this.gender = gender;
        this.providerType = providerType;
        this.roleType = roleType;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
