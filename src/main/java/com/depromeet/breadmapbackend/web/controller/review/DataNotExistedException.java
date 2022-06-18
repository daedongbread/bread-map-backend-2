package com.depromeet.breadmapbackend.web.controller.review;

public class DataNotExistedException extends RuntimeException {
    public DataNotExistedException() { super(); }

    public DataNotExistedException(String message) {
        super(message);
    }

    public DataNotExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
