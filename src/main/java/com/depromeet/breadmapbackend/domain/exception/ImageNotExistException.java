package com.depromeet.breadmapbackend.domain.exception;

public class ImageNotExistException extends RuntimeException {
    public ImageNotExistException() { super("Image is not existed."); }
}
