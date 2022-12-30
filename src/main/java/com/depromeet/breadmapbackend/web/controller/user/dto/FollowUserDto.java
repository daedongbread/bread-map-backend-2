package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowUserDto {
    private Long userId;
    private String userImage;
    private String nickName;
    private Integer reviewNum;
    private Integer followerNum;
    private Boolean isFollow;
    private Boolean isMe;

    @Builder
    public FollowUserDto(User user, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe) {
        this.userId = user.getId();
        this.userImage = user.getImage();
        this.nickName = user.getNickName();
        this.reviewNum = reviewNum;
        this.followerNum = followerNum;
        this.isFollow = isFollow;
        this.isMe = isMe;
    }
}
