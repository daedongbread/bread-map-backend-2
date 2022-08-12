package com.depromeet.breadmapbackend.domain.review;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    BLOCK,
    UNBLOCK;
}
