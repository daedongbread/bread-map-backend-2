package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.common.SliceResponseDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/bakery/{bakeryId}") // TODO
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getBakeryReviewList(
            @CurrentUser String username, @PathVariable Long bakeryId,
            @RequestParam(defaultValue = "latest") ReviewSortType sortBy, @RequestParam int page){
        return new ApiResponse<>(reviewService.getBakeryReviewList(username, bakeryId, sortBy, page));
    }

    @GetMapping("/bakery/{bakeryId}/product/{productId}") // TODO
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getProductReviewList(
            @CurrentUser String username, @PathVariable Long bakeryId, @PathVariable Long productId,
            @RequestParam(defaultValue = "latest") ReviewSortType sortBy, @RequestParam int page){
        return new ApiResponse<>(reviewService.getProductReviewList(username, bakeryId, productId, sortBy, page));
    }

    @GetMapping("/user/{userId}") // TODO
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getUserReviewList(
            @CurrentUser String username, @PathVariable Long userId,
            @RequestParam(defaultValue = "latest") ReviewSortType sortBy, @RequestParam int page){
        return new ApiResponse<>(reviewService.getUserReviewList(username, userId, page));
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReviewDetailDto> getReview(@CurrentUser String username, @PathVariable Long reviewId) {
        return new ApiResponse<>(reviewService.getReview(username, reviewId));
    }

    @PostMapping("/{bakeryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReview(@CurrentUser String username, @PathVariable Long bakeryId, @RequestPart ReviewRequest request,
                              @RequestPart(required = false) List<MultipartFile> files) throws IOException {
        reviewService.addReview(username, bakeryId, request, files);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReview(@CurrentUser String username, @PathVariable Long reviewId) {
        reviewService.removeReview(username, reviewId);
    }

    @PostMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewLike(@CurrentUser String username, @PathVariable Long reviewId) {
        reviewService.reviewLike(username, reviewId);
    }

    @DeleteMapping("/{reviewId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewUnlike(@CurrentUser String username, @PathVariable Long reviewId) {
        reviewService.reviewUnlike(username, reviewId);
    }

    @GetMapping("/{reviewId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReviewCommentDto>> getReviewCommentList(@PathVariable Long reviewId) {
        return new ApiResponse<>(reviewService.getReviewCommentList(reviewId));
    }

    @PostMapping("/{reviewId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReviewComment(@CurrentUser String username, @PathVariable Long reviewId, @RequestBody ReviewCommentRequest request) {
        reviewService.addReviewComment(username, reviewId, request);
    }

    @DeleteMapping("/{reviewId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReviewComment(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewService.removeReviewComment(username, reviewId, commentId);
    }

    @PostMapping("/{reviewId}/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewCommentLike(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewService.reviewCommentLike(username, reviewId, commentId);
    }

    @DeleteMapping("/{reviewId}/comment/{commentId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewCommentUnlike(@CurrentUser String username, @PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewService.reviewCommentUnlike(username, reviewId, commentId);
    }

    @PostMapping("/{reviewId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewReport(
            @CurrentUser String username, @PathVariable Long reviewId, @RequestBody ReviewReportRequest request) {
        reviewService.reviewReport(username, reviewId, request);
    }
}
