package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.search.HotKeyword;
import com.depromeet.breadmapbackend.domain.admin.search.HotKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotKeywordServiceImpl implements HotKeywordService {

	private final HotKeywordRepository hotKeywordRepository;
	@Override
	public List<HotKeyword> getHotKeywordsRanking() {
		return hotKeywordRepository.findAllByOrderByRankingAsc();
	}
}
