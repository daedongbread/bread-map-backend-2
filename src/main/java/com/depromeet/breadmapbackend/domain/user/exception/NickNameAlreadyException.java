package com.depromeet.breadmapbackend.domain.user.exception;

public class NickNameAlreadyException extends RuntimeException {
    public NickNameAlreadyException() { super("This nickname is already existed."); }
}
