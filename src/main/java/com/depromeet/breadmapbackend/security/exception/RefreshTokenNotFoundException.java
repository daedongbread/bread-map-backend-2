package com.depromeet.breadmapbackend.security.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("리프레쉬 토큰을 찾을 수 없습니다.");
    }
}
