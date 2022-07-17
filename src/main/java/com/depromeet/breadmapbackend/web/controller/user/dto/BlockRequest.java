package com.depromeet.breadmapbackend.web.controller.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockRequest {
    private Long userId;

    @Builder
    public BlockRequest(Long userId) {
        this.userId = userId;
    }
}
