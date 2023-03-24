package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.domain.review.dto.ReviewCommentDto;
import com.depromeet.breadmapbackend.domain.review.dto.ReviewCommentRequest;
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
            @CurrentUser String username, @PathVariable Long reviewId,
            @RequestBody @Validated(ValidationSequence.class) ReviewCommentRequest request) {
        reviewCommentService.addReviewComment(username, reviewId, request);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReviewComment(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.removeReviewComment(username, reviewId, commentId);
    }

    @PostMapping("/{reviewId}/comments/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewCommentLike(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.reviewCommentLike(username, reviewId, commentId);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewCommentUnlike(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.reviewCommentUnlike(username, reviewId, commentId);
    }
}
