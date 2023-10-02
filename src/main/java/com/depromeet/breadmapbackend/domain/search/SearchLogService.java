package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.search.dto.SearchLog;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface SearchLogService {
    void saveRecentSearchLog(Long id, String name);
    List<SearchLog> getRecentSearchLogs(Long id);
    void deleteRecentSearchLog(Long id, String name, String createdAt);
}
