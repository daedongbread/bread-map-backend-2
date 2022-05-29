package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BakeryNotFoundException extends RuntimeException {
    public BakeryNotFoundException() {
        super("빵집을 찾을 수 없습니다.");
    }
}