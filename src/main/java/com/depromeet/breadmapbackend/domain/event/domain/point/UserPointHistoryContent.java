package com.depromeet.breadmapbackend.domain.event.domain.point;

public record UserPointHistoryContent(int point, int grandTotalPoint, PointHistoryType type, String description) {
}
