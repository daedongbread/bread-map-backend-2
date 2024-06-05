package com.depromeet.breadmapbackend.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@RequiredArgsConstructor
@Getter
@ToString
public enum BreadDiaryEventState {
    PENDING("대기"), ACCEPTED("수락"), REJECTED("거부");

    private final String value;

    @JsonCreator
    public static BreadDiaryEventState jsonCreator(String state) {
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
