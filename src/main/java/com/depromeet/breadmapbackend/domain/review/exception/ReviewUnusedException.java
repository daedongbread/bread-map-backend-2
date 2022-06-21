package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewUnusedException extends RuntimeException {
    public ReviewUnusedException() { super("Review is unused."); }
}
