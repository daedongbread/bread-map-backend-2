package com.depromeet.breadmapbackend.domain.event;

import lombok.AllArgsConstructor;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@AllArgsConstructor
public enum BreadDiaryEventState {
    PENDING("대기"), ACCEPTED("수락"), REJECTED("거부");
    public final String value;

    @JsonCreator
    static public BreadDiaryEventState jsonCreator(String state) {
        try {
            return BreadDiaryEventState.valueOf(state);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}
