package com.depromeet.breadmapbackend.domain.admin.search.dto;

/**
 * KeywordStatResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public record KeywordStatResponse(
	Long id,
	String keyword,
	long oneWeekCount,
	long oneMonthCount,
	long threeMonthCount
) {
}
