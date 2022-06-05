package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagBakeryNotFoundException extends RuntimeException {
    public FlagBakeryNotFoundException() {
        super("해당 리스트에서 없는 빵집입니다.");
    }
}