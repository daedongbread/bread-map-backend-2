package com.depromeet.breadmapbackend.web.controller.search;

import com.depromeet.breadmapbackend.service.search.SearchService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/auto")
    @ResponseStatus(HttpStatus.OK) //TODO : 레디스로 전환
    public ApiResponse<List<SearchDto>> autoComplete(@RequestParam String word,
                                                     @RequestParam Double latitude, @RequestParam Double longitude) {
        return new ApiResponse<>(searchService.autoComplete(word, latitude, longitude));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SearchDto>> search(@CurrentUser String username, @RequestParam String word,
                                               @RequestParam Double latitude, @RequestParam Double longitude) {
        return new ApiResponse<>(searchService.search(username, word, latitude, longitude));
    }

    @GetMapping("/keywords")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<String>> recentKeywords(@CurrentUser String username) {
        return new ApiResponse<>(searchService.recentKeywords(username));
    }

    @DeleteMapping("/keywords")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecentKeyword(@CurrentUser String username, @RequestParam String keyword) {
        searchService.deleteRecentKeyword(username, keyword);
    }

    @DeleteMapping("/keywords/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecentKeywordAll(@CurrentUser String username) {
        searchService.deleteRecentKeywordAll(username);
    }
}
