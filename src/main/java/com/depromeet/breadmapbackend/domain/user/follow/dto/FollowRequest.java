package com.depromeet.breadmapbackend.domain.user.follow.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {
    @NotNull(message = "유저 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Long userId;
}
