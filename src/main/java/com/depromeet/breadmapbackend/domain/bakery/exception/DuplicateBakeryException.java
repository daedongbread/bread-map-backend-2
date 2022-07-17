package com.depromeet.breadmapbackend.domain.bakery.exception;

public class DuplicateBakeryException extends RuntimeException{
    public DuplicateBakeryException() {
        super("Bakery is already exists");
    }
}
