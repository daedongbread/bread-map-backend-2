package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BakeryReviewScoreDto {
    private Long bakeryId;
    private Double totalScore;
    private Long reviewCount;
}
