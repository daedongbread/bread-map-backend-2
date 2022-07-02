package com.depromeet.breadmapbackend.domain.user.exception;

public class FollowAlreadyException extends RuntimeException {
    public FollowAlreadyException() { super("Already follow."); }
}
