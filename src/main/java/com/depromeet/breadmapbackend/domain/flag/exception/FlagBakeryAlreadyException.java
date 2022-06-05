package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagBakeryAlreadyException extends RuntimeException {
    public FlagBakeryAlreadyException() { super("이미 해당 리스트에 등록된 빵집입니다."); }
}
