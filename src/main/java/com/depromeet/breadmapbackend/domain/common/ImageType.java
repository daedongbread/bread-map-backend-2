package com.depromeet.breadmapbackend.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    BAKERY_IMAGE("bakeryImage"),
    BREAD_IMAGE("breadImage"),
    REVIEW_IMAGE("reviewImage"),
    BAKERY_DELETE_REPORT_IMAGE("bakeryDeleteReportImage"),
    BREAD_ADD_REPORT_IMAGE("breadAddReportImage");

    private final String code;
}
