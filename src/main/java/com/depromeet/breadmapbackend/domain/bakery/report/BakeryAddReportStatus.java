package com.depromeet.breadmapbackend.domain.bakery.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BakeryAddReportStatus {
    BEFORE_REFLECT("BEFORE_REFLECT"), // 검토전
    NOT_REFLECT("NOT_REFLECT"), // 미반영
    REFLECT("REFLECT") // 반영완료
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BakeryAddReportStatus findByCode(String code) {
        return Stream.of(BakeryAddReportStatus.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
