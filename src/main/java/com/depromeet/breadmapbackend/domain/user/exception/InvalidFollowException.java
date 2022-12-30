package com.depromeet.breadmapbackend.domain.user.exception;

public class InvalidFollowException extends RuntimeException {
    public InvalidFollowException() { super("Invalid follow, unfollow."); }
}
