package com.depromeet.breadmapbackend.domain.bakery.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BakeryAddEvent {
    private Long userId;
    private Long bakeryId;
    private String bakeryName;
}
