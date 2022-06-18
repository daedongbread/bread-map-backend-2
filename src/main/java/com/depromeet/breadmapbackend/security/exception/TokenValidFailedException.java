package com.depromeet.breadmapbackend.security.exception;

public class TokenValidFailedException extends RuntimeException {
    public TokenValidFailedException() {
        super("Token is invalid");
    }
}
