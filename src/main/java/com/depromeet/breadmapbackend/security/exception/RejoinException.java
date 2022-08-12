package com.depromeet.breadmapbackend.security.exception;

public class RejoinException extends RuntimeException {
    public RejoinException() { super("Rejoin is not possible within 7 days of withdrawal."); }
}
