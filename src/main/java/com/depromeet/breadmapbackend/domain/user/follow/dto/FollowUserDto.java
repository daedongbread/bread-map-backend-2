package com.depromeet.breadmapbackend.domain.user.follow.dto;

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
    private Boolean isFollow; // 내가 팔로우 했는지
    private Boolean isMe;

    @Builder
    public FollowUserDto(User user, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe) {
        this.userId = user.getId();
        this.userImage = user.getUserInfo().getImage();
        this.nickName = user.getUserInfo().getNickName();
        this.reviewNum = reviewNum;
        this.followerNum = followerNum;
        this.isFollow = isFollow;
        this.isMe = isMe;
    }
}
