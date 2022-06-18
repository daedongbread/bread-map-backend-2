package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BakeryNotFoundException extends RuntimeException {
    public BakeryNotFoundException() {
        super("Bakery is not existed.");
    }
}