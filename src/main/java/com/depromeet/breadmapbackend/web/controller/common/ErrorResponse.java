package com.depromeet.breadmapbackend.web.controller.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private Integer code;
    private String message;
}
