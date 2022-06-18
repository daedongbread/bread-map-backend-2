package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagUnEditException extends RuntimeException {
    public FlagUnEditException() { super("List cannot be edited or deleted."); }
}
