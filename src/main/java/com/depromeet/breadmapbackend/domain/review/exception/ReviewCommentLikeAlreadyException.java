package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewCommentLikeAlreadyException extends RuntimeException {
    public ReviewCommentLikeAlreadyException() { super("Already review comment like."); }
}
