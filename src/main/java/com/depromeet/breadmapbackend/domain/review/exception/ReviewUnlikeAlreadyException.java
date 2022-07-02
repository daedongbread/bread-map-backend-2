package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewUnlikeAlreadyException extends RuntimeException {
    public ReviewUnlikeAlreadyException() { super("Already review unlike."); }
}
