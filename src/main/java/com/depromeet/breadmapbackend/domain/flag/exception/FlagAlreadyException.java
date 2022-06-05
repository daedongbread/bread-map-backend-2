package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagAlreadyException extends RuntimeException {
    public FlagAlreadyException() { super("이미 존재하는 리스트입니다."); }
}
