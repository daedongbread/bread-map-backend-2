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

    private List<UserFlagDto> userFlagDtoList;
    private List<UserReviewDto> userReviewDtoList;

    @Builder
    public ProfileDto(User user, Integer followingNum, Integer followerNum, List<UserFlagDto> userFlagDtoList, List<UserReviewDto> userReviewDtoList) {
        this.userId = user.getId();
        this.userImage = user.getProfileImageUrl();
        this.nickName = user.getNickName();
        this.followingNum = followingNum;
        this.followerNum = followerNum;
        this.userFlagDtoList = userFlagDtoList;
        this.userReviewDtoList = userReviewDtoList;
    }
}
