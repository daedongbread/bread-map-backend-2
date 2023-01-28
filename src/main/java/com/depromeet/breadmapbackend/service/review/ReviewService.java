package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.web.controller.common.SliceResponseDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    SliceResponseDto<ReviewDto> getBakeryReviewList(String username, Long bakeryId, ReviewSortType sortBy, Pageable pageable);
    ReviewDetailDto getReview(String username, Long reviewId);
    ReviewAddDto addReview(String username, Long reviewId, ReviewRequest request);
    void addReviewImage(String username, Long reviewId, List<MultipartFile> files) throws IOException;
    void addReviewTest(String username, Long reviewId, ReviewRequest request, List<MultipartFile> files) throws IOException;
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
