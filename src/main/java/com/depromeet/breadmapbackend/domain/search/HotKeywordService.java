package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.search.HotKeyword;

import java.util.List;

public interface HotKeywordService {
	List<HotKeyword> getHotKeywordsRanking();

}
