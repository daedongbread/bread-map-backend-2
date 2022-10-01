package com.depromeet.breadmapbackend.domain.admin.exception;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException() { super("Admin is not existed."); }
}
