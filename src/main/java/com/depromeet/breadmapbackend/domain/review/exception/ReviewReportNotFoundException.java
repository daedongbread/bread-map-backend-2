package com.depromeet.breadmapbackend.domain.review.exception;

public class ReviewReportNotFoundException extends RuntimeException {
    public ReviewReportNotFoundException() { super("Review report is not existed."); }
}
