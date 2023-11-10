package com.depromeet.breadmapbackend.domain.admin.search.dto;

/**
 * HotKeywordResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public record HotKeywordResponse(
	Long id,
	String keyword,
	int rank
) {
}
