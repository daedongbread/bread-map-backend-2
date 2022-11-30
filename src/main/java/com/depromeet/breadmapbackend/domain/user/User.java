package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.security.domain.ProviderType;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
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

    private String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flag> flagList = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Builder
    private User(String username, String nickName, String email, Gender gender,
                 ProviderType providerType, RoleType roleType, String image) {
        this.username = username;
        this.nickName = nickName;
        this.email = email;
        this.gender = gender;
        this.providerType = providerType;
        this.roleType = roleType;
        this.image = image;
        this.status = UserStatus.UNBLOCK;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void addFlag(Flag flag) {
        this.flagList.add(flag);
    }

    public void removeFlag(Flag flag) { this.flagList.remove(flag); }

    public void changeBlock() {
        if(this.status.equals(UserStatus.BLOCK)) this.status = UserStatus.UNBLOCK;
        else this.status = UserStatus.BLOCK;
    }

//    @Override
//    public boolean equals(Object object) {
//        User user = (User) object;
//        if (user.getUsername().equals(this.getUsername())) {
//            return true;
//        }
//        return false;
//    }
}
