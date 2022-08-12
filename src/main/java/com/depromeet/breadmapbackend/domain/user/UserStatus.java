package com.depromeet.breadmapbackend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    BLOCK,
    UNBLOCK;
}
