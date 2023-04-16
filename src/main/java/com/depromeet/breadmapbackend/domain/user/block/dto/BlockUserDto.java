package com.depromeet.breadmapbackend.domain.user.block.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockUserDto {
    private Long userId;
    private String userImage;
    private String nickName;
    private Integer reviewNum;
    private Integer followerNum;

    @Builder
    public BlockUserDto(User user, Integer reviewNum, Integer followerNum) {
        this.userId = user.getId();
        this.userImage = user.getUserInfo().getImage();
        this.nickName = user.getUserInfo().getNickName();
        this.reviewNum = reviewNum;
        this.followerNum = followerNum;
    }
}
