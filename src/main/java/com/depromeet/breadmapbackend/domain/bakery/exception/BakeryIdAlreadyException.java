package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BakeryIdAlreadyException extends RuntimeException {
    public BakeryIdAlreadyException() { super("This bakery id is already existed."); }
}
