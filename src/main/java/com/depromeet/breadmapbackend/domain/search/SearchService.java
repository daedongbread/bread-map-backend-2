package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchEngineDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchType;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.SearchResultResponse;

import java.util.HashSet;
import java.util.List;

public interface SearchService {
    List<SearchDto> searchDatabase(String oAuthId, String word, Double latitude, Double longitude);
    SearchResultResponse searchEngine(String oAuthId, String word, Double latitude, Double longitude, SearchType searchType);
    List<String> searchKeywordSuggestions(String word);
}
