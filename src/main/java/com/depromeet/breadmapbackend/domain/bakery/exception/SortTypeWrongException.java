package com.depromeet.breadmapbackend.domain.bakery.exception;

public class SortTypeWrongException extends RuntimeException {
    public SortTypeWrongException() {
        super("Sort type is wrong.");
    }
}
