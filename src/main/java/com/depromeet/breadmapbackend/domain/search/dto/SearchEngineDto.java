package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchEngineDto {
    private Long bakeryId;
    private String bakeryName;
    private Long breadId;
    private String breadName;
    private String address;
    private Double distance;

    public static List<Long> extractBakeryIdList(List<SearchEngineDto> searchEngineDtoList) {
        return searchEngineDtoList.stream()
                .map(SearchEngineDto::getBakeryId)
                .collect(Collectors.toList());
    }
}
