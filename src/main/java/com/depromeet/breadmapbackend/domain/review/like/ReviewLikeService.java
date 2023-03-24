package com.depromeet.breadmapbackend.domain.review.like;

public interface ReviewLikeService {
    void reviewLike(String username, Long reviewId);
    void reviewUnlike(String username, Long reviewId);
}
