package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BreadNotFoundException extends RuntimeException {
    public BreadNotFoundException() { super("Bread is not existed."); }
}
