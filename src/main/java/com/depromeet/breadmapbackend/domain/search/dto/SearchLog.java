package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchLog implements Serializable {
    private static final long serialVersionUID = 2369269236230L;
    private String keyword;
    private String createdAt;

}
