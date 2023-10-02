package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenSearchIndex {
    BREAD_SEARCH("bread-search", "v1"),
    BAKERY_SEARCH("bakery-search", "v1");

    private final String lowerCase;
    private final String version;

    public String getIndexNameWithVersion() {
        return lowerCase + "-" + version;
    }
}
