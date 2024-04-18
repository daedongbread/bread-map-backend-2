package com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent;

import lombok.AllArgsConstructor;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@AllArgsConstructor
public enum BreadDiaryEventCheckState {
    PENDING("대기"), ACCEPTED("수락"), REJECTED("거부");
    public final String value;

    @JsonCreator
    static public BreadDiaryEventCheckState jsonCreator(String state) {
        try {
            return BreadDiaryEventCheckState.valueOf(state);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}
