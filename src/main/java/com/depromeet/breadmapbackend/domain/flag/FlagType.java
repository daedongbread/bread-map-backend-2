package com.depromeet.breadmapbackend.domain.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlagType {

    NONE("안간 곳"),
    GONE("가본 곳"),
    PICKED("가볼 곳");

    private final String code;

}
