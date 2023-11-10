package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.List;

/**
 * AdminHotKeywordService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public interface AdminHotKeywordService {
	List<Keyword> getHotKeywords(SortType sortType);

	List<HotKeyword> getHotKeywordsRank();

	void updateHotKeywordsRank(List<HotKeyword> hotKeywords);

}
