package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewLikeAlreadyException extends RuntimeException {
    public ReviewLikeAlreadyException() { super("Already review like."); }
}
