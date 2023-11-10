package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.search.dto.HotKeywordResponse;
import com.depromeet.breadmapbackend.domain.admin.search.dto.HotKeywordUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.search.dto.KeywordStatResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * AdminSearchKeywordController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */

@RestController
@RequestMapping("/v1/admin/search/hot-keywords")
@RequiredArgsConstructor
public class AdminHotKeywordController {

	private final AdminHotKeywordService adminHotKeywordService;

	@GetMapping
	ApiResponse<List<KeywordStatResponse>> getHotKeywordsByKeyword(
		@RequestParam(name = "sortType", required = false, defaultValue = "THREE_MONTH") String sortType
	) {
		return new ApiResponse<>(
			adminHotKeywordService.getHotKeywords(SortType.valueOf(sortType.toUpperCase()))
				.stream()
				.map(Mapper::of)
				.toList()
		);
	}

	@GetMapping("/rank")
	ApiResponse<List<HotKeywordResponse>> getHotKeywords() {
		return new ApiResponse<>(
			adminHotKeywordService.getHotKeywordsRank()
				.stream()
				.map(Mapper::of)
				.toList()
		);
	}

	@PutMapping("/rank")
	@ResponseStatus(HttpStatus.ACCEPTED)
	void updateHotKeywords(@RequestBody HotKeywordUpdateRequest request) {
		adminHotKeywordService.updateHotKeywordsRank(
			request.HotKeywordList()
				.stream()
				.map(HotKeywordUpdateRequest.HotKeywordInfo::toEntity)
				.toList()
		);
	}
}
