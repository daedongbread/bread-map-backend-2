package com.depromeet.breadmapbackend.domain.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeDayType {
    TODAY("오늘"),
    WEEK("이번주"),
    BEFORE("이전 활동");

    private final String code;
}
