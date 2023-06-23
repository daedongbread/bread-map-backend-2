package com.depromeet.breadmapbackend.domain.review;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.dto.ReviewDetailDto;
import com.depromeet.breadmapbackend.domain.review.dto.ReviewDto;
import com.depromeet.breadmapbackend.domain.review.dto.ReviewRequest;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface ReviewService {
	List<Review> getReviewList(User me, Bakery bakery);

	PageResponseDto<ReviewDto> getBakeryReviewList(String oAuthId, Long bakeryId, ReviewSortType sortBy, int page);

	PageResponseDto<ReviewDto> getProductReviewList(String oAuthId, Long bakeryId, Long productId,
		ReviewSortType sortBy, int page);

	PageResponseDto<ReviewDto> getUserReviewList(String oAuthId, Long userId, int page);

	ReviewDetailDto getReview(String oAuthId, Long reviewId);

	void addReview(String oAuthId, Long reviewId, ReviewRequest request);

	void removeReview(String oAuthId, Long reviewId);

	List<Review> getReviewListInBakeries(Long userId, List<Bakery> bakeries);
}
