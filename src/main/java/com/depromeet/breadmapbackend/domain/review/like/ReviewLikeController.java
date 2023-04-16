package com.depromeet.breadmapbackend.domain.review.like;

import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewLike(@CurrentUser String oAuthId, @PathVariable Long reviewId) {
        reviewLikeService.reviewLike(oAuthId, reviewId);
    }

    @DeleteMapping("/{reviewId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewUnlike(@CurrentUser String oAuthId, @PathVariable Long reviewId) {
        reviewLikeService.reviewUnlike(oAuthId, reviewId);
    }
}
