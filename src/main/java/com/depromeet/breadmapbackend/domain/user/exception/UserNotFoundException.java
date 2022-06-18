package com.depromeet.breadmapbackend.domain.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User is not existed");
    }
}
