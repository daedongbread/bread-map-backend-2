package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchEngineDto;

import java.util.List;

public interface SearchService {
    List<SearchDto> searchDatabase(String oAuthId, String word, Double latitude, Double longitude);
    List<SearchEngineDto> searchEngine(String oAuthId, String word, Double latitude, Double longitude);
}
