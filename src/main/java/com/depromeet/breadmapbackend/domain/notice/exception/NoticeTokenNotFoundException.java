package com.depromeet.breadmapbackend.domain.notice.exception;

public class NoticeTokenNotFoundException extends RuntimeException {
    public NoticeTokenNotFoundException() { super("Notice Token is not existed."); }
}
