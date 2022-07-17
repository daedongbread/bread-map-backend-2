package com.depromeet.breadmapbackend.domain.notice.exception;

public class NoticeTypeWrongException extends RuntimeException {
    public NoticeTypeWrongException() { super("Notice type is wrong."); }
}
