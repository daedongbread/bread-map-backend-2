package com.depromeet.breadmapbackend.domain.search.dto.keyword.response;

import com.depromeet.breadmapbackend.domain.search.dto.SearchResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultResponse {

    private String subwayStationName;
    private List<SearchResultDto> searchResultDtoList;

}
