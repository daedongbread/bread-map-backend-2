package com.depromeet.breadmapbackend.domain.notice.exception;

public class NoticeTokenAlreadyException extends RuntimeException {
    public NoticeTokenAlreadyException() { super("Notice token is already existed."); }
}
