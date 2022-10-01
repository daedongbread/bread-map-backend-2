package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class FollowRequest {
    @NotBlank(message = "유저 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Long userId;

    @Builder
    public FollowRequest(Long userId) {
        this.userId = userId;
    }
}
