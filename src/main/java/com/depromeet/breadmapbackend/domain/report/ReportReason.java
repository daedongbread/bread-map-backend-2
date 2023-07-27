package com.depromeet.breadmapbackend.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ReportReason
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */

@RequiredArgsConstructor
@Getter
public enum ReportReason {

	IRRELEVANT_CONTENT("IRRELEVANT_CONTENT"), // 관계없는 내용
	INAPPROPRIATE_CONTENT("INAPPROPRIATE_CONTENT"), // 음란성, 욕설 등 부적절한 내용
	IRRELEVANT_IMAGE("IRRELEVANT_IMAGE"), // 관련없는 사진 게시
	UNFIT_CONTENT("UNFIT_CONTENT"), //  작성 취지에 맞지 않는 내용(복사글 등)
	COPYRIGHT_THEFT("COPYRIGHT_THEFT"), // 저작권 도용 의심(사진 등)
	ETC("ETC") // 기타(하단 내용 작성)
	;

	private final String code;
}

