package com.depromeet.breadmapbackend.domain.exception;

public class ImageNumExceedException extends RuntimeException {
    public ImageNumExceedException() { super("Number of images exceeded."); }
}
