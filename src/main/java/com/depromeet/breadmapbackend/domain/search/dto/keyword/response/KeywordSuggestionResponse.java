package com.depromeet.breadmapbackend.domain.search.dto.keyword.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordSuggestionResponse {
    HashSet<String> keywordSuggestions;
}
