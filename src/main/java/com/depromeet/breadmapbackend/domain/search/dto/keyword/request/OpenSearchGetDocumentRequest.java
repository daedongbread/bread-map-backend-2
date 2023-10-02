package com.depromeet.breadmapbackend.domain.search.dto.keyword.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@NoArgsConstructor
public class OpenSearchGetDocumentRequest {
    String indexName;
    String id;
}
