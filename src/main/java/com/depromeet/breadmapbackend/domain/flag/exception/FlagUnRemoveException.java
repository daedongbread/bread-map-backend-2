package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagUnRemoveException extends RuntimeException {
    public FlagUnRemoveException() { super("삭제할 수 없는 리스트입니다."); }
}
