package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.CommonLoadData;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface OpenSearchService {
    OpenSearchCreateIndexResponse deleteAndCreateIndex(String indexName) throws IOException;
    void deleteIndex(OpenSearchIndex openSearchIndex, Long targetId) throws IOException;
    IndexResponse addDataToIndex(String indexName, HashMap<String, String> stringMapping) throws IOException;
    SearchResponse getBakeryByKeyword(String keyword);
    SearchResponse getBreadByKeyword(String keyword);
    SearchResponse getDocumentByGeology(String indexName, Double latitude, Double longitude);
    SearchResponse getKeywordSuggestions(OpenSearchIndex openSearchIndex, String keyword);
    void loadEntireData() throws IOException;
    void convertDataAndLoadToEngine(String indexName, List<? extends CommonLoadData> loadList) throws IOException;

}
