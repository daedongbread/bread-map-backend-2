package com.depromeet.breadmapbackend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    BLOCK("차단"),
    UNBLOCK("비차단");

    private final String code;
}
