package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.domain.review.dto.ReviewCommentDto;
import com.depromeet.breadmapbackend.domain.review.dto.ReviewCommentRequest;

import java.util.List;

public interface ReviewCommentService {
    List<ReviewCommentDto> getReviewCommentList(Long reviewId);
    void addReviewComment(String username, Long reviewId, ReviewCommentRequest request);
    void removeReviewComment(String username, Long reviewId, Long commentId);
    void reviewCommentLike(String username, Long reviewId, Long commentId);
    void reviewCommentUnlike(String username, Long reviewId, Long commentId);
}
