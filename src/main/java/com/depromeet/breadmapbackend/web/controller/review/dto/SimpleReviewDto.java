package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleReviewDto {
    private Long id;
    private String image;
    private Double rating;

    @Builder
    public SimpleReviewDto(Review review) {
        this.id = review.getId();
        if (review.getImageList().isEmpty()) this.image = null;
        else this.image = review.getImageList().get(0).getImage();
        this.rating = Math.floor(
                review.getRatings().stream().map(BreadRating::getRating).mapToLong(Long::longValue)
                        .average().orElse(0) * 10) / 10.0;
    }
}
