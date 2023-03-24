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
    public void reviewLike(@CurrentUser String username, @PathVariable Long reviewId) {
        reviewLikeService.reviewLike(username, reviewId);
    }

    @DeleteMapping("/{reviewId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewUnlike(@CurrentUser String username, @PathVariable Long reviewId) {
        reviewLikeService.reviewUnlike(username, reviewId);
    }
}
