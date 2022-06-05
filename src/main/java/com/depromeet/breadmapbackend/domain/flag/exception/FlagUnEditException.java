package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagUnEditException extends RuntimeException {
    public FlagUnEditException() { super("수정 또는 삭제할 수 없는 리스트입니다."); }
}
