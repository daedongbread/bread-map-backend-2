package com.depromeet.breadmapbackend.web.controller.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewAddDto {
    private Long reviewId;

    @Builder
    public ReviewAddDto(Long reviewId) {
        this.reviewId = reviewId;
    }
}
