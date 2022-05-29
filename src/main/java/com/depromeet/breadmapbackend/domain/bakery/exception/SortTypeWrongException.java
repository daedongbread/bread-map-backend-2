package com.depromeet.breadmapbackend.domain.bakery.exception;

public class SortTypeWrongException extends RuntimeException {
    public SortTypeWrongException() {
        super("정렬 타입이 틀렸습니다.");
    }
}
