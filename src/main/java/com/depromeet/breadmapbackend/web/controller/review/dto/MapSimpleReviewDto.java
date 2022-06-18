package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.BreadReview;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapSimpleReviewDto {
    private Long id;
    private String content;

    public MapSimpleReviewDto(BreadReview review) {
        this.id = review.getId();
        this.content = review.getContent();
    }
}
