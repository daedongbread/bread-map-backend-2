package com.depromeet.breadmapbackend.domain.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalType {
    BEFORE_CHECKING("챌린지 인증내역 심사 전"),
    APPROVED("심사 결과 승인"),
    NOT_APPROVED("심사 결과 반려");

    private final String description;
}
