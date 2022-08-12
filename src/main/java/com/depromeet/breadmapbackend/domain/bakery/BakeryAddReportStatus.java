package com.depromeet.breadmapbackend.domain.bakery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryAddReportStatus {
    before_reflect("검토전"),
    not_reflect("미반영"),
    reflect("반영완료");

    private final String code;
}
