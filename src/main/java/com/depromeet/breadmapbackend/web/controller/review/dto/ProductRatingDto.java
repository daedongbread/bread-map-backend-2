package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRatingDto {
    private String productName;
    private Long rating;

    @Builder
    public ProductRatingDto(ReviewProductRating reviewProductRating) {
        this.productName = reviewProductRating.getProduct().getName();
        this.rating = reviewProductRating.getRating();
    }
}
