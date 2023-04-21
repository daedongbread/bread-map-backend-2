package com.depromeet.breadmapbackend.domain.review;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    LATEST("LATEST"), // 최신순
    HIGH("HIGH"), // 별점 높은 순
    LOW("LOW") // 별점 낮은 순
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReviewSortType findByCode(String code) {
        return Stream.of(ReviewSortType.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
