package com.depromeet.breadmapbackend.domain.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DaedongException extends RuntimeException {
    private final DaedongStatus status;
}
