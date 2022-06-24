package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewCommentUnlikeAlreadyException extends RuntimeException {
    public ReviewCommentUnlikeAlreadyException() { super("Already review comment unlike."); }
}
