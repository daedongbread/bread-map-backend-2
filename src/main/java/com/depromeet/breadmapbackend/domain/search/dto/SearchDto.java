package com.depromeet.breadmapbackend.domain.search.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchDto {
    private Long bakeryId;
    private String bakeryName;
    private String address;
    private Double rating;
    private Integer reviewNum;
    private Double distance;

    @Builder
    public SearchDto(Bakery bakery, Double rating, Integer reviewNum, Double distance) {
        this.bakeryId = bakery.getId();
        this.bakeryName = bakery.getName();
        this.address = bakery.getAddress();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.distance = distance;
    }
}
