package com.depromeet.breadmapbackend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedLikeResponse {

    private String status;
    private int likeCounts;

    @Builder
    public FeedLikeResponse(String status, int likeCounts) {
        this.status = status;
        this.likeCounts = likeCounts;
    }
}
