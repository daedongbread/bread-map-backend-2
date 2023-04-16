package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @Column(nullable = false, unique = true)
    private String nickName;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String image;

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateImage(String image) {
        this.image = image;
    }
}
