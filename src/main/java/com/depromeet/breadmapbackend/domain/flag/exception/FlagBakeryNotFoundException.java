package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagBakeryNotFoundException extends RuntimeException {
    public FlagBakeryNotFoundException() {
        super("Bakery is not on this list.");
    }
}