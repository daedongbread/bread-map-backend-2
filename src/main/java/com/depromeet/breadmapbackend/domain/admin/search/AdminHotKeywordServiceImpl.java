package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * AdminHotKeywordServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */

@Service
@RequiredArgsConstructor
public class AdminHotKeywordServiceImpl implements AdminHotKeywordService {

	private final HotKeywordRepository hotKeywordRepository;

	@Override
	public List<Keyword> getHotKeywords(SortType sortType) {
		return Keyword.getMockData().stream()
			.sorted(Comparator.comparing((Keyword keyword) ->
				switch (sortType) {
					case ONE_WEEK -> keyword.getOneWeekCount();
					case ONE_MONTH -> keyword.getOneMonthCount();
					case THREE_MONTH -> keyword.getThreeMonthCount();
				}).reversed())
			.toList();
	}

	@Override
	public List<HotKeyword> getHotKeywordsRanking() {
		return hotKeywordRepository.findAllByOrderByRankingAsc();
	}

	@Override
	public void updateHotKeywordsRanking(final List<HotKeyword> hotKeywords) {
		checkDuplicateKeywords(hotKeywords);
		checkDuplicateRank(hotKeywords);

		hotKeywordRepository.deleteAll(); // 최대 저장 키워드 50개
		hotKeywordRepository.saveAll(hotKeywords);
	}

	private void checkDuplicateKeywords(final List<HotKeyword> hotKeywords) {
		Map<String, Long> keywordCount = hotKeywords.stream()
			.collect(Collectors.groupingBy(HotKeyword::getKeyword, Collectors.counting()));
		if (keywordCount.values().stream().anyMatch(count -> count > 1)) {
			throw new DaedongException(DaedongStatus.DUPLICATED_KEYWORD);
		}
	}

	private void checkDuplicateRank(final List<HotKeyword> hotKeywords) {
		Map<Integer, Long> keywordCount = hotKeywords.stream()
			.collect(Collectors.groupingBy(HotKeyword::getRanking, Collectors.counting()));
		if (keywordCount.values().stream().anyMatch(count -> count > 1)) {
			throw new DaedongException(DaedongStatus.DUPLICATED_RANK);
		}
	}
}
