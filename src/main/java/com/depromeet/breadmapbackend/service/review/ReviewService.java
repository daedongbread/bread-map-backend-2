package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    PageResponseDto<ReviewDto> getBakeryReviewList(String username, Long bakeryId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getProductReviewList(String username, Long bakeryId, Long productId, ReviewSortType sortBy, int page);
    PageResponseDto<ReviewDto> getUserReviewList(String username, Long userId, int page);
    ReviewDetailDto getReview(String username, Long reviewId);
    ReviewAddDto addReview(String username, Long reviewId, ReviewRequest request);
    void addReviewImage(String username, Long reviewId, List<MultipartFile> files) throws IOException;
    void addReviewTest(String username, Long reviewId, ReviewRequest request, List<MultipartFile> files) throws IOException;
    void removeReview(String username, Long reviewId);
    void reviewLike(String username, Long reviewId);
    void reviewUnlike(String username, Long reviewId);
    List<ReviewCommentDto> getReviewCommentList(Long reviewId);
    void addReviewComment(String username, Long reviewId, ReviewCommentRequest request);
    void removeReviewComment(String username, Long reviewId, Long commentId);
    void reviewCommentLike(String username, Long reviewId, Long commentId);
    void reviewCommentUnlike(String username, Long reviewId, Long commentId);
    void reviewReport(String username, Long reviewId, ReviewReportRequest request);
}
