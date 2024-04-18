package com.depromeet.breadmapbackend.domain.event.domain.point;

import com.depromeet.breadmapbackend.domain.event.domain.Timestamp;
import lombok.Getter;

@Getter
public record UserPointHistory(UserPointHistoryPK pk, UserPointHistoryContent content, Timestamp timestamp) {
}
