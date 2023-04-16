package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    PageResponseDto<ReviewDto> getBakeryReviewList(String oAuthId, Long bakeryId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getProductReviewList(String oAuthId, Long bakeryId, Long productId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getUserReviewList(String oAuthId, Long userId, int page);
    ReviewDetailDto getReview(String oAuthId, Long reviewId);
    void addReview(String oAuthId, Long reviewId, ReviewRequest request);
    void removeReview(String oAuthId, Long reviewId);
}
