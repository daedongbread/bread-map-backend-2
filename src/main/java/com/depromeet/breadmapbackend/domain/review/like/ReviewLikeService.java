package com.depromeet.breadmapbackend.domain.review.like;

public interface ReviewLikeService {
    void reviewLike(String oAuthId, Long reviewId);
    void reviewUnlike(String oAuthId, Long reviewId);
}
