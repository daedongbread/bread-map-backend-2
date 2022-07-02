package com.depromeet.breadmapbackend.domain.user.exception;

public class FollowNotFoundException extends RuntimeException {
    public FollowNotFoundException() { super("Follow is not existed."); }
}
