package com.depromeet.breadmapbackend.security.exception;

public class UserBlockException extends RuntimeException {
    public UserBlockException() { super("This user is blocked."); }
}
