package com.depromeet.breadmapbackend.domain.admin.openSearch;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.request.OpenSearchCreateIndexRequest;
import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.search.OpenSearchService;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/admin/search-engine")
@RequiredArgsConstructor
public class OpenSearchAdminController {
    private final OpenSearchService openSearchService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OpenSearchCreateIndexResponse> createIndex(
            @Valid @RequestBody OpenSearchCreateIndexRequest createIndexRequest) throws IOException {
        return new ApiResponse<>(openSearchService.deleteAndCreateIndex(createIndexRequest.getIndexName()));
    }
}
