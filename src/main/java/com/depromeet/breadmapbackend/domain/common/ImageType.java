package com.depromeet.breadmapbackend.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType { // TODO : with ConfigurationProperties
    BAKERY_IMAGE("bakeryImage"),
    PRODUCT_IMAGE("productImage"),
    REVIEW_IMAGE("reviewImage"),
    BAKERY_REPORT_IMAGE("bakeryReportImage"),
    BAKERY_UPDATE_REPORT_IMAGE("bakeryUpdateReportImage"),
    PRODUCT_ADD_REPORT_IMAGE("productAddReportImage"),
    USER_IMAGE("userImage"),
    ADMIN_TEMP_IMAGE("adminTempImage");

    private final String code;
}
