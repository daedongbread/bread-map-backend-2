package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.validation.LowerCase;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SearchResponse> getDocumentByKeyword(
            @RequestParam @LowerCase String indexName,
            @RequestParam(required = false) String keyword) {
        SearchResponse documentResponse = openSearchService.getDocumentByKeyword(indexName, keyword);
        return new ApiResponse<>(documentResponse);
    }
}
