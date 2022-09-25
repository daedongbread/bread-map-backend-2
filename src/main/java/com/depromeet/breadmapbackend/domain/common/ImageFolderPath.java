package com.depromeet.breadmapbackend.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageFolderPath {
    BAKERY_IMAGE,
    BREAD_IMAGE,
    REVIEW_IMAGE,
    BAKERY_DELETE_REPORT_IMAGE,
    BREAD_ADD_REPORT_IMAGE;
}
