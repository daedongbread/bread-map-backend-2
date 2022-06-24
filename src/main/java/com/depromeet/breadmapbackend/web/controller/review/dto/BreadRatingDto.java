package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BreadRatingDto {
    private String breadName;
    private Long rating;

    @Builder
    public BreadRatingDto(BreadRating breadRating) {
        this.breadName = breadRating.getBread().getName();
        this.rating = breadRating.getRating();
    }
}
