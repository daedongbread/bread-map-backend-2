package com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent;

import lombok.Getter;

@Getter
public record BreadDiaryEventCheckPK(long id) {

    public enum State {
        PENDING, ACCEPTED, REJECTED
    }
}
