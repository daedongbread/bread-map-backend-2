package com.depromeet.breadmapbackend.global.infra.feign.exception;

public class FeignException extends RuntimeException {
    public FeignException() { super("Feign Exception."); }
}
