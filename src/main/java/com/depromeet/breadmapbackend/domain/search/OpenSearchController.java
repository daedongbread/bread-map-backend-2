package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.request.OpenSearchAddDataRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/search-engine/document")
@RequiredArgsConstructor
public class OpenSearchController {
    private final OpenSearchService openSearchService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<IndexResponse> addDataToIndex(
            @Valid @RequestBody OpenSearchAddDataRequest addDataRequest) throws IOException {
        return new ApiResponse<>(openSearchService.addDataToIndex(addDataRequest.getIndexName(), addDataRequest.getStringMapping()));
    }

    @PostMapping("/load")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Boolean> postBakeryAdnBread() throws IOException {

        openSearchService.deleteAndCreateIndex(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());
        openSearchService.deleteAndCreateIndex(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());

        openSearchService.loadEntireData();

        return new ApiResponse<>(Boolean.TRUE);
    }


    @GetMapping("/bread")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SearchResponse> getBreadByKeyword(
            @RequestParam(required = false) String keyword) {
        SearchResponse documentResponse = openSearchService.getBreadByKeyword(keyword);
        return new ApiResponse<>(documentResponse);
    }

    @GetMapping("/bakery")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SearchResponse> getBakeryByKeyword(
            @RequestParam(required = false) String keyword) {
        SearchResponse documentResponse = openSearchService.getBakeryByKeyword(keyword);
        return new ApiResponse<>(documentResponse);
    }
}
