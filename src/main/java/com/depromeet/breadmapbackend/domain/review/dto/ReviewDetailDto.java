package com.depromeet.breadmapbackend.domain.review.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.review.Review;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewDetailDto {
	private ReviewDto reviewDto;
	private List<SimpleReviewDto> userOtherReviews;
	private List<SimpleReviewDto> bakeryOtherReviews;

	@Builder
	public ReviewDetailDto(Review review, Integer reviewNum, Integer followerNum, Boolean isFollow, Boolean isMe,
		Boolean isLike,
		List<SimpleReviewDto> userOtherReviews, List<SimpleReviewDto> bakeryOtherReviews, Long commentNum) {
		this.reviewDto = ReviewDto.builder()
			.review(review)
			.reviewNum(reviewNum)
			.followerNum(followerNum)
			.isFollow(isFollow)
			.isMe(isMe)
			.isLike(isLike)
			.commentNum(commentNum)
			.build();
		this.userOtherReviews = userOtherReviews;
		this.bakeryOtherReviews = bakeryOtherReviews;
	}
}
