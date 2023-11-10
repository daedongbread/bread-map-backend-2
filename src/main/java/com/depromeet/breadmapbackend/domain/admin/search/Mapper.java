package com.depromeet.breadmapbackend.domain.admin.search;

import com.depromeet.breadmapbackend.domain.admin.search.dto.HotKeywordResponse;
import com.depromeet.breadmapbackend.domain.admin.search.dto.KeywordStatResponse;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public class Mapper {

	public static HotKeywordResponse of(HotKeyword hotKeywords) {
		return new HotKeywordResponse(
			hotKeywords.getId(),
			hotKeywords.getKeyword(),
			hotKeywords.getRank()
		);
	}

	public static KeywordStatResponse of(Keyword keyword) {
		return new KeywordStatResponse(
			keyword.getId(),
			keyword.getKeyword(),
			keyword.getOneWeekCount(),
			keyword.getOneMonthCount(),
			keyword.getThreeMonthCount()
		);
	}
}
