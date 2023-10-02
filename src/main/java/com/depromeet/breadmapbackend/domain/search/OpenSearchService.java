package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;

import java.io.IOException;
import java.util.HashMap;

public interface OpenSearchService {
    OpenSearchCreateIndexResponse createIndex(String indexName) throws IOException;
    AcknowledgedResponse deleteIndex(String indexName) throws IOException;
    IndexResponse addDataToIndex(String indexName, HashMap<String, String> stringMapping) throws IOException;
    SearchResponse getDocumentByKeyword(String indexName, String keyword);

    void loadData() throws IOException;
}
