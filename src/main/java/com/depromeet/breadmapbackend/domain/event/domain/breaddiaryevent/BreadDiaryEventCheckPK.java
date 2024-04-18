package com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent;

public record BreadDiaryEventCheckPK(long id) {

    public enum State {
        PENDING, ACCEPTED, REJECTED
    }
}
