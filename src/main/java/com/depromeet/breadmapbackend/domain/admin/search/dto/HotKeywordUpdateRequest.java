package com.depromeet.breadmapbackend.domain.admin.search.dto;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.depromeet.breadmapbackend.domain.admin.search.HotKeyword;

/**
 * HotKeywordUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public record HotKeywordUpdateRequest(
	@Size(min = 1) List<HotKeywordInfo> HotKeywordList
) {

	public record HotKeywordInfo(
		@NotNull
		String keyword,
		@NotNull
		@Max(50)
		int rank
	) {
		public HotKeyword toEntity() {
			return HotKeyword.createSearchKeyword(keyword, rank);
		}
	}
}