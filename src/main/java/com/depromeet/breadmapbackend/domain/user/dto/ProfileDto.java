package com.depromeet.breadmapbackend.domain.user.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileDto {
    private Long userId;
    private String userImage;
    private String nickName;
    private Integer followingNum;
    private Integer followerNum;
    private Boolean isFollow;

    @Builder
    public ProfileDto(User user, Integer followingNum, Integer followerNum, Boolean isFollow) {
        this.userId = user.getId();
        this.userImage = user.getUserInfo().getImage();
        this.nickName = user.getUserInfo().getNickName();
        this.followingNum = followingNum;
        this.followerNum = followerNum;
        this.isFollow = isFollow;
    }
}
