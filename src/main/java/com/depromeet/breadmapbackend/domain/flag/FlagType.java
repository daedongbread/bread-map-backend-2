package com.depromeet.breadmapbackend.domain.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlagType {

    NONE("미선택"),
    PICKED("가고싶어요"),
    GONE("가봤어요");

    private final String code;

}
