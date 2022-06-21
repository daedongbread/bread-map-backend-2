package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() { super("Review is not existed."); }
}
