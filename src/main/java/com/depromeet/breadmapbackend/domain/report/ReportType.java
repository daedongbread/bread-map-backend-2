package com.depromeet.breadmapbackend.domain.report;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ReportType
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
@RequiredArgsConstructor
@Getter
public enum ReportType {
	FREE_TALK,
	BREAD_STORY,
	EATEN_BREAD,
	REVIEW,
	COMMENT;

	public static ReportType of(final String type) {
		return Arrays.stream(ReportType.values())
			.filter(reportType -> reportType.name().equals(type))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_REPORT_TYPE));
	}
}
