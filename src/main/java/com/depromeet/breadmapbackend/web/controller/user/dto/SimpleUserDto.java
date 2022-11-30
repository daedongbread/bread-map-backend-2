package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleUserDto {
    private Long userId;
    private String userImage;
    private String nickName;
    private Integer reviewNum;
    private Integer followerNum;

    @Builder
    public SimpleUserDto(User user, Integer reviewNum, Integer followerNum) {
        this.userId = user.getId();
        this.userImage = user.getImage();
        this.nickName = user.getNickName();
        this.reviewNum = reviewNum;
        this.followerNum = followerNum;
    }
}
