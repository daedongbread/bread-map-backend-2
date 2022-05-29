package com.depromeet.breadmapbackend.domain.bakery.exception;

public class HeartAlreadyException extends RuntimeException {
    public HeartAlreadyException() {
        super("이미 가고싶어요에 등록된 빵집입니다.");
    }
}

