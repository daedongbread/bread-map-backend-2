package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenSearchIndex {
    BREAD_SEARCH("bread-search", "x1","breadName"),
    BAKERY_SEARCH("bakery-search", "x1","bakeryName");

    private final String lowerCase;
    private final String version;
    private final String fieldName;

    public String getIndexNameWithVersion() {
        return lowerCase + "-" + version;
    }
}
