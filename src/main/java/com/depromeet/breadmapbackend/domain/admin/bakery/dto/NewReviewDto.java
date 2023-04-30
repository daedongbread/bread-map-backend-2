package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class NewReviewDto {
    private Long reviewId;
    private LocalDateTime createdAt;
    private String nickName;
    private List<ProductRatingDto> productRatingList;
    private String content;
    private List<ReviewImageDto> imageList;

    @Getter
    @NoArgsConstructor
    public static class ProductRatingDto {
        private String productName;
        private Long rating;

        @Builder
        public ProductRatingDto(ReviewProductRating reviewProductRating) {
            this.productName = reviewProductRating.getProduct().getName();
            this.rating = reviewProductRating.getRating();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReviewImageDto {
        private Long imageId;
        private String image;
        private Boolean isRegistered;

        @Builder
        public ReviewImageDto(ReviewImage reviewImage) {
            this.imageId = reviewImage.getId();
            this.image = reviewImage.getImage();
            this.isRegistered = reviewImage.getIsRegistered();
        }
    }

    @Builder
    public NewReviewDto(Review review) {
        this.reviewId = review.getId();
        this.createdAt = review.getCreatedAt();
        this.nickName = review.getUser().getNickName();
        this.productRatingList = review.getRatings().stream().map(ProductRatingDto::new).collect(Collectors.toList());
        this.content = review.getContent();
        this.imageList = review.getImageList().stream().map(ReviewImageDto::new).collect(Collectors.toList());
    }
}
