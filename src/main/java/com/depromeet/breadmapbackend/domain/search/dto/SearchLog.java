package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLog implements Serializable {
    private String keyword;
    private String createdAt;

}
