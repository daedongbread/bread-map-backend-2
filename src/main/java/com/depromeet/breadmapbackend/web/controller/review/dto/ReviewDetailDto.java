package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewDetailDto {
    private ReviewDto reviewDto;
    private List<SimpleReviewDto> userOtherReviews;
    private List<SimpleReviewDto> bakeryOtherReviews;

    @Builder
    public ReviewDetailDto(Review review, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe,
                           List<SimpleReviewDto> userOtherReviews, List<SimpleReviewDto> bakeryOtherReviews) {
        this.reviewDto = ReviewDto.builder()
                .review(review).reviewNum(reviewNum).followerNum(followerNum).isFollow(isFollow).isMe(isMe).build();
        this.userOtherReviews = userOtherReviews;
        this.bakeryOtherReviews = bakeryOtherReviews;
    }
}
