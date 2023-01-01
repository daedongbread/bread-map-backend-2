package com.depromeet.breadmapbackend.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DaedongException extends RuntimeException {
    private final DaedongStatus daedongStatus;
}
