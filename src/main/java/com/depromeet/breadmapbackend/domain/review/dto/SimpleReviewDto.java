package com.depromeet.breadmapbackend.domain.review.dto;

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
        this.rating = review.getAverageRating();
    }
}
