package com.depromeet.breadmapbackend.domain.bakery.exception;

public class FlagAlreadyException extends RuntimeException {
    public FlagAlreadyException() {
        super("이미 플래그된 빵집입니다.");
    }
}
