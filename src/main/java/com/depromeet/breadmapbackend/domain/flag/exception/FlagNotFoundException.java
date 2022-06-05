package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagNotFoundException extends RuntimeException {
    public FlagNotFoundException() { super("해당 리스트를 찾을 수 없습니다."); }
}
