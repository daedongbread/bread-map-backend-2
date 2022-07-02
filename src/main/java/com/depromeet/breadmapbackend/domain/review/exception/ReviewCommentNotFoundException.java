package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewCommentNotFoundException extends RuntimeException {
    public ReviewCommentNotFoundException() { super("Comment is not existed."); }
}
