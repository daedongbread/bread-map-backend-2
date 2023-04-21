package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/bakeries/{bakeryId}") // TODO : 상하관계
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getBakeryReviewList(
            @CurrentUser String oAuthId, @PathVariable Long bakeryId,
            @RequestParam(defaultValue = "latest") @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class) ReviewSortType sortBy, @RequestParam int page){
        return new ApiResponse<>(reviewService.getBakeryReviewList(oAuthId, bakeryId, sortBy, page));
    }

    @GetMapping("/bakeries/{bakeryId}/products/{productId}") // TODO : 상하관계
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getProductReviewList(
            @CurrentUser String oAuthId, @PathVariable Long bakeryId, @PathVariable Long productId,
            @RequestParam(defaultValue = "latest") @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class) ReviewSortType sortBy,
            @RequestParam int page){
        return new ApiResponse<>(reviewService.getProductReviewList(oAuthId, bakeryId, productId, sortBy, page));
    }

    @GetMapping("/users/{userId}") // TODO : 상하관계
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<ReviewDto>> getUserReviewList(
            @CurrentUser String oAuthId, @PathVariable Long userId, @RequestParam int page){
        return new ApiResponse<>(reviewService.getUserReviewList(oAuthId, userId, page));
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReviewDetailDto> getReview(@CurrentUser String oAuthId, @PathVariable Long reviewId) {
        return new ApiResponse<>(reviewService.getReview(oAuthId, reviewId));
    }

    @PostMapping("/bakeries/{bakeryId}") // TODO : 상하관계
    @ResponseStatus(HttpStatus.CREATED)
    public void addReview(
            @CurrentUser String oAuthId, @PathVariable Long bakeryId,
            @RequestBody @Validated(ValidationSequence.class) ReviewRequest request) {
        reviewService.addReview(oAuthId, bakeryId, request);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReview(@CurrentUser String oAuthId, @PathVariable Long reviewId) {
        reviewService.removeReview(oAuthId, reviewId);
    }
}
