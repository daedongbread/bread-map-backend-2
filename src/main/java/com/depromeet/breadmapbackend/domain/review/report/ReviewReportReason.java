package com.depromeet.breadmapbackend.domain.review.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ReviewReportReason {
    IRRELEVANT_CONTENT("IRRELEVANT_CONTENT"), // 리뷰와 관계없는 내용
    INAPPROPRIATE_CONTENT("INAPPROPRIATE_CONTENT"), // 음란성, 욕설 등 부적절한 내용
    IRRELEVANT_IMAGE("IRRELEVANT_IMAGE"), // 리뷰와 관련없는 사진 게시
    UNFIT_CONTENT("UNFIT_CONTENT"), // 리뷰 작성 취지에 맞지 않는 내용(복사글 등)
    COPYRIGHT_THEFT("COPYRIGHT_THEFT"), // 저작권 도용 의심(사진 등)
    ETC("ETC") // 기타(하단 내용 작성)
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReviewReportReason findByCode(String code) {
        return Stream.of(ReviewReportReason.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
