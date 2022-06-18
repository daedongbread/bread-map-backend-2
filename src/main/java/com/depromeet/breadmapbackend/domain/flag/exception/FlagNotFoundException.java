package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagNotFoundException extends RuntimeException {
    public FlagNotFoundException() { super("List is not existed"); }
}
