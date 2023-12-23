package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private Long bakeryId;
    private String bakeryName;
    private Long breadId;
    private String breadName;
    private String address;
    private Double distance;
    private Long reviewNum;
    private Double totalScore;

    private Double latitude;
    private Double longitude;
    private List<String> bakeryImageUrl;

    private Integer flagCount;
    private String flagColor;
}
