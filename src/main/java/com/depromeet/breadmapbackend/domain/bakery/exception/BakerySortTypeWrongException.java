package com.depromeet.breadmapbackend.domain.bakery.exception;

public class BakerySortTypeWrongException extends RuntimeException {
    public BakerySortTypeWrongException() {
        super("Bakery sort type is wrong.");
    }
}
