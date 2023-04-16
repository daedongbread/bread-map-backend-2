package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentDto;
import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService reviewCommentService;

    @GetMapping("/{reviewId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReviewCommentDto>> getReviewCommentList(@PathVariable Long reviewId) {
        return new ApiResponse<>(reviewCommentService.getReviewCommentList(reviewId));
    }

    @PostMapping("/{reviewId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReviewComment(
            @CurrentUser String oAuthId, @PathVariable Long reviewId,
            @RequestBody @Validated(ValidationSequence.class) ReviewCommentRequest request) {
        reviewCommentService.addReviewComment(oAuthId, reviewId, request);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReviewComment(@CurrentUser String oAuthId, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.removeReviewComment(oAuthId, reviewId, commentId);
    }

    // TODO 댓글 신고

    @PostMapping("/{reviewId}/comments/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewCommentLike(@CurrentUser String oAuthId, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.reviewCommentLike(oAuthId, reviewId, commentId);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewCommentUnlike(@CurrentUser String oAuthId, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.reviewCommentUnlike(oAuthId, reviewId, commentId);
    }
}
