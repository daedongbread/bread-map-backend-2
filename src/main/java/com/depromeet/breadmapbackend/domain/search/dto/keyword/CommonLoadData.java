package com.depromeet.breadmapbackend.domain.search.dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonLoadData {
    private Long bakeryId;
    private String bakeryName;
    private String bakeryAddress;
    private Double longitude;
    private Double latitude;
    private Double totalScore;
    private Long reviewCount;
}
