package com.depromeet.breadmapbackend.domain.review;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    BLOCK("차단"),
    UNBLOCK("비차단");

    private final String code;
}
