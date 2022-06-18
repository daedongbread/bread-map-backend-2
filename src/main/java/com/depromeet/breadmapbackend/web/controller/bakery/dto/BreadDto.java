package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bread;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BreadDto {
    private Long id;
    private String name;
    private Double rating;
    private Integer reviewNum;
    private Integer price;

    @Builder
    public BreadDto(Bread bread, Double rating, Integer reviewNum) {
        this.id = bread.getId();
        this.name = bread.getName();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.price = bread.getPrice();
    }
}
