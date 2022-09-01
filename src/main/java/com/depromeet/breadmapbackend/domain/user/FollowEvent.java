package com.depromeet.breadmapbackend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowEvent {
    private Long userId;
    private Long fromUserId;
}
