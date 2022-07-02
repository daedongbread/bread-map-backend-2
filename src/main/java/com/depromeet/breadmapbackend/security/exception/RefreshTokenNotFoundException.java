package com.depromeet.breadmapbackend.security.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("Refresh Token is not existed");
    }
}
