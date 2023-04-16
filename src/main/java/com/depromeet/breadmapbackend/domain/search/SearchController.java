package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/auto")
    @ResponseStatus(HttpStatus.OK) //TODO : 레디스로 전환
    public ApiResponse<List<SearchDto>> autoComplete(
            @RequestParam
            @NotBlank(message = "검색어는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
            @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
            String word,
            @RequestParam Double latitude, @RequestParam Double longitude) {
        return new ApiResponse<>(searchService.autoComplete(word, latitude, longitude));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SearchDto>> search(
            @CurrentUser String oAuthId,
            @RequestParam
            @NotBlank(message = "검색어는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
            @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
            String word,
            @RequestParam Double latitude, @RequestParam Double longitude) {
        return new ApiResponse<>(searchService.search(oAuthId, word, latitude, longitude));
    }

    @GetMapping("/keywords")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<String>> recentKeywords(@CurrentUser String oAuthId) {
        return new ApiResponse<>(searchService.recentKeywords(oAuthId));
    }

    @DeleteMapping("/keywords")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecentKeyword(@CurrentUser String oAuthId, @RequestParam String keyword) {
        searchService.deleteRecentKeyword(oAuthId, keyword);
    }

    @DeleteMapping("/keywords/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecentKeywordAll(@CurrentUser String oAuthId) {
        searchService.deleteRecentKeywordAll(oAuthId);
    }
}
