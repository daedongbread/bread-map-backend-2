package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchDto {
    private Long bakeryId;
    private String bakeryName;
    private Integer reviewNum;
    private Double distance;

    @Builder
    public SearchDto(Long bakeryId, String bakeryName, Integer reviewNum, Double distance) {
        this.bakeryId = bakeryId;
        this.bakeryName = bakeryName;
        this.reviewNum = reviewNum;
        this.distance = distance;
    }
}
