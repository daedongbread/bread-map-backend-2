package com.depromeet.breadmapbackend.domain.bakery.exception;

public class UnheartAlreadyException extends RuntimeException {
    public UnheartAlreadyException() {
        super("이미 가고싶어요에서 제거된 빵집입니다.");
    }
}
