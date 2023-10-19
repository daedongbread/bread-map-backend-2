package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
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
    SearchResponse getDocumentByGeology(String indexName, Double latitude, Double longitude);
    SearchResponse getKeywordSuggestions(OpenSearchIndex openSearchIndex, String keyword);

    void loadEntireData() throws IOException;

    void loadHourlyData() throws IOException;
}
