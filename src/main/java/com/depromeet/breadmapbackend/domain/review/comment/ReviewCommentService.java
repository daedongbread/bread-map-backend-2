package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentDto;
import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentRequest;

import java.util.List;

public interface ReviewCommentService {
    List<ReviewCommentDto> getReviewCommentList(Long reviewId);
    void addReviewComment(String oAuthId, Long reviewId, ReviewCommentRequest request);
    void removeReviewComment(String oAuthId, Long reviewId, Long commentId);
    void reviewCommentLike(String oAuthId, Long reviewId, Long commentId);
    void reviewCommentUnlike(String oAuthId, Long reviewId, Long commentId);
}
