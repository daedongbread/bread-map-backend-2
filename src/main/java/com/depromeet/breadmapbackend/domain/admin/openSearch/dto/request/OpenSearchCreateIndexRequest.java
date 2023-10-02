package com.depromeet.breadmapbackend.domain.admin.openSearch.dto.request;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.validation.LowerCase;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class OpenSearchCreateIndexRequest {
    @LowerCase String indexName;
}
