package com.depromeet.breadmapbackend.domain.bakery.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryAddReportStatus {
    BEFORE_REFLECT("검토전"),
    NOT_REFLECT("미반영"),
    REFLECT("반영완료");

    private final String code;
}
