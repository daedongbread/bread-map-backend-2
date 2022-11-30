package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProfileDto {
    private Long userId;
    private String userImage;
    private String nickName;
    private Integer followingNum;
    private Integer followerNum;
    private Boolean isMe;
    private Boolean isFollow;

    private List<UserFlagDto> userFlagList;
    private List<UserReviewDto> userReviewList;

    @Builder
    public ProfileDto(
            User user, Integer followingNum, Integer followerNum,
            List<UserFlagDto> userFlagList, List<UserReviewDto> userReviewList,
            Boolean isMe, Boolean isFollow) {
        this.userId = user.getId();
        this.userImage = user.getProfileImageUrl();
        this.nickName = user.getNickName();
        this.followingNum = followingNum;
        this.followerNum = followerNum;
        this.userFlagList = userFlagList;
        this.userReviewList = userReviewList;
        this.isMe = isMe;
        this.isFollow = isFollow;
    }
}
