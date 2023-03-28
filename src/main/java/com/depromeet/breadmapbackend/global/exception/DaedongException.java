package com.depromeet.breadmapbackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DaedongException extends RuntimeException {
    private final DaedongStatus daedongStatus;
}
