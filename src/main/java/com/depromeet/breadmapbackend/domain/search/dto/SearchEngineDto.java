package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Double rating;
    private Integer reviewNum;
    private Double distance;
}
