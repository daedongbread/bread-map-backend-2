package com.depromeet.breadmapbackend.web.controller.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private List<BreadRatingRequest> breadRatingList;
    private String content;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BreadRatingRequest {
        private Long breadId;
        private Long rating;
    }
}
