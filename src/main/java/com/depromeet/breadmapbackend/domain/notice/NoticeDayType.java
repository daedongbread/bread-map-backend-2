package com.depromeet.breadmapbackend.domain.notice;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum NoticeDayType {
    TODAY("today"), // 오늘
    WEEK("week"), // 이번주
    BEFORE("before") // 이전 활동
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static NoticeDayType findByCode(String code) {
        return Stream.of(NoticeDayType.values())
                .filter(c -> c.code.equals(code)) // 소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
