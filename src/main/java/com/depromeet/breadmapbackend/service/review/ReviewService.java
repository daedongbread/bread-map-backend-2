package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    List<ReviewDto> getBakeryReviewList(Long bakeryId, ReviewSortType sort);
    ReviewDetailDto getReview(Long reviewId);
    void addReview(String username, Long bakeryId, ReviewRequest request, List<MultipartFile> files) throws IOException;
    void removeReview(String username, Long reviewId);
//    List<UserReviewDto> getUserReviewList(String username);
    void reviewLike(String username, Long reviewId);
    void reviewUnlike(String username, Long reviewId);
    List<ReviewCommentDto> getReviewCommentList(Long reviewId);
    void addReviewComment(String username, Long reviewId, ReviewCommentRequest request);
    void removeReviewComment(String username, Long reviewId, Long commentId);
    void reviewCommentLike(String username, Long reviewId, Long commentId);
    void reviewCommentUnlike(String username, Long reviewId, Long commentId);
    void reviewReport(String username, Long reviewId, ReviewReportRequest request);
}
