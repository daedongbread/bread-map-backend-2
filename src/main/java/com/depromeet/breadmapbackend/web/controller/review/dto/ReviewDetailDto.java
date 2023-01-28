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
    private BakeryInfo bakeryInfo;
    private ReviewDto reviewDto;
    private List<ReviewCommentDto> comments;
    private List<SimpleReviewDto> userOtherReviews;
    private List<SimpleReviewDto> bakeryOtherReviews;

    @Getter
    @NoArgsConstructor
    public static class BakeryInfo {
        private Long bakeryId;
        private String bakeryImage;
        private String bakeryName;
        private String bakeryAddress;

        @Builder
        public BakeryInfo(Bakery bakery) {
            this.bakeryId = bakery.getId();
            this.bakeryImage = bakery.getImage();
            this.bakeryName = bakery.getName();
            this.bakeryAddress = bakery.getAddress();
        }
    }

    @Builder
    public ReviewDetailDto(Review review, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe,
                           List<SimpleReviewDto> userOtherReviews, List<SimpleReviewDto> bakeryOtherReviews) {
        this.bakeryInfo = BakeryInfo.builder().bakery(review.getBakery()).build();
        this.reviewDto = ReviewDto.builder()
                .review(review).reviewNum(reviewNum).followerNum(followerNum).isFollow(isFollow).isMe(isMe).build();
        this.comments = review.getComments().stream()
                .filter(reviewComment -> reviewComment.getParent() == null).map(ReviewCommentDto::new).collect(Collectors.toList());
        this.userOtherReviews = userOtherReviews;
        this.bakeryOtherReviews = bakeryOtherReviews;
    }
}
