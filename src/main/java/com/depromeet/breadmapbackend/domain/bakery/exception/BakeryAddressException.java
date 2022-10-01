package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BakeryAddressException extends RuntimeException {
    public BakeryAddressException() { super("Bakery Address is not invalid."); }
}
