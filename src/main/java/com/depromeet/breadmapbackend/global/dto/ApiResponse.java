package com.depromeet.breadmapbackend.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final T data;
}
