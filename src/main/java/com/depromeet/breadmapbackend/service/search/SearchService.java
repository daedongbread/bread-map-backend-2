package com.depromeet.breadmapbackend.service.search;

import com.depromeet.breadmapbackend.web.controller.search.dto.SearchDto;

import java.util.List;

public interface SearchService {
    List<SearchDto> autoComplete(String word, Double latitude, Double longitude);
    SearchDto search(String username, String word, Double latitude, Double longitude);
    List<String> recentKeywords(String username);
    void deleteRecentKeyword(String username, String keyword);
    void deleteRecentKeywordAll(String username);
}
