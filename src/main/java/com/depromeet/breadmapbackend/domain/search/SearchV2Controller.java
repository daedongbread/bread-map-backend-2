package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchEngineDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchLog;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.KeywordSuggestionResponse;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.RecentKeywords;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v2/search")
@RequiredArgsConstructor
public class SearchV2Controller {

    private final SearchService searchService;
    private final SearchLogService searchLogService;

    @GetMapping("/keyword")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SearchEngineDto>> searchKeyword(
            @CurrentUser String oAuthId,
            @RequestParam
            @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
            String keyword,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<SearchEngineDto> adminUserForEventPost = searchService.searchEngine(oAuthId, keyword, latitude, longitude);

        return new ApiResponse<>(adminUserForEventPost);
    }

    @GetMapping("/recent")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RecentKeywords> searchRecent(
            @CurrentUser String oAuthId
    ) {
        List<SearchLog> adminUserForEventPost = searchLogService.getRecentSearchLogs(oAuthId);
        List<String> recentKeywords = adminUserForEventPost.stream().map(SearchLog::getKeyword).toList();

        return new ApiResponse<>(RecentKeywords.builder().recentKeywords(recentKeywords)
                .build());
    }

    @GetMapping("/suggestions")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<KeywordSuggestionResponse> searchKeywordSuggestions(@RequestParam String keyword) {
        HashSet<String> keywordSuggestions = searchService.searchKeywordSuggestions(keyword);

        return new ApiResponse<>(KeywordSuggestionResponse.builder().keywordSuggestions(keywordSuggestions).build());
    }
}
