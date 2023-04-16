package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;

import java.util.List;

public interface SearchService {
    List<SearchDto> autoComplete(String word, Double latitude, Double longitude);
    List<SearchDto> search(String oAuthId, String word, Double latitude, Double longitude);
    List<String> recentKeywords(String oAuthId);
    void deleteRecentKeyword(String oAuthId, String keyword);
    void deleteRecentKeywordAll(String oAuthId);
}
