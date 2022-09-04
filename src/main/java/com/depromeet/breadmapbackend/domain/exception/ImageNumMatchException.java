package com.depromeet.breadmapbackend.domain.exception;

public class ImageNumMatchException extends RuntimeException {
    public ImageNumMatchException() { super("Number of images is not match."); }
}
