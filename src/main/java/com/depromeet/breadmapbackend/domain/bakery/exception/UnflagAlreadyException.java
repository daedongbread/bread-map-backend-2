package com.depromeet.breadmapbackend.domain.bakery.exception;

public class UnflagAlreadyException extends RuntimeException {
    public UnflagAlreadyException() {
        super("이미 언플래그된 빵집입니다.");
    }
}
