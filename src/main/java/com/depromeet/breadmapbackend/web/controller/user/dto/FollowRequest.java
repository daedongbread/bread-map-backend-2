package com.depromeet.breadmapbackend.web.controller.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequest {
    private Long userId;

    @Builder
    public FollowRequest(Long userId) {
        this.userId = userId;
    }
}
