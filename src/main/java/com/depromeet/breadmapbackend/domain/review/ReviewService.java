package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    PageResponseDto<ReviewDto> getBakeryReviewList(String username, Long bakeryId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getProductReviewList(String username, Long bakeryId, Long productId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getUserReviewList(String username, Long userId, int page);
    ReviewDetailDto getReview(String username, Long reviewId);
    void addReview(String username, Long reviewId, ReviewRequest request, List<MultipartFile> files) throws IOException;
    void removeReview(String username, Long reviewId);
}
