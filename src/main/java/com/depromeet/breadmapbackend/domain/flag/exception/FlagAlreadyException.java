package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagAlreadyException extends RuntimeException {
    public FlagAlreadyException() { super("List already existed."); }
}
