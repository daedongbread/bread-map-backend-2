package com.depromeet.breadmapbackend.domain.review.dto;

import com.depromeet.breadmapbackend.domain.review.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapSimpleReviewDto {
    private Long id;
    private String content;

    public MapSimpleReviewDto(Review review) {
        this.id = review.getId();
        this.content = review.getContent();
    }
}
