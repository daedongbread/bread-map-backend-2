package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BreadAlreadyException extends RuntimeException {
    public BreadAlreadyException() { super("Bread is already exist."); }
}
