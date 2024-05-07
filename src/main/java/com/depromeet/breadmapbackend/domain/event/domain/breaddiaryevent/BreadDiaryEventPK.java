package com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent;

public record BreadDiaryEventPK(long id) {

    public enum State {
        PENDING, ACCEPTED, REJECTED
    }
}
