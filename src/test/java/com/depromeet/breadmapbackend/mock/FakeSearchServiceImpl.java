package com.depromeet.breadmapbackend.mock;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.search.SearchService;
import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchResultDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchType;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.SearchResultResponse;

import java.util.Collections;
import java.util.List;

/**
 * FakeSearchServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/13/23
 */

public class FakeSearchServiceImpl implements SearchService {

	@Override
	public List<SearchDto> searchDatabase(final String oAuthId, final String word, final Double latitude,
		final Double longitude) {
		return null;
	}

	@Override
	public SearchResultResponse searchEngine(
		final String oAuthId,
		final String word,
		final Double latitude,
		final Double longitude,
		final SearchType searchType
	) {
		SearchResultDto searchResultDto = SearchResultDto.builder()
			.bakeryId(1L)
			.bakeryName("Test Bakery")
			.breadId(1L)
			.breadName("Test Bread")
			.address("Test Address")
			.totalScore(5d)
			.reviewNum(5L)
			.distance(100d)
			.bakeryImageUrl(Collections.singletonList("https://test.com"))
			.latitude(123d)
			.longitude(123d)
			.flagCount(123)
			.flagColor(FlagColor.ORANGE.getCode())
			.build();

		return SearchResultResponse
			.builder()
			.subwayStationName("역삼역")
			.searchResultDtoList(List.of(searchResultDto))
			.build();
	}

	@Override
	public List<String> searchKeywordSuggestions(final String word) {
		return List.of("test1", "test2", "test3");
	}
}
