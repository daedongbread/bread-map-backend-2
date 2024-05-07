package com.depromeet.breadmapbackend.domain.event.domain.point;

import lombok.Getter;

@Getter
public record UserPointHistoryContent(int point, int grandTotalPoint, PointHistoryType type, String description) {
}
