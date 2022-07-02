package com.depromeet.breadmapbackend.web.controller.flag.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlagBakeryCardDto {
    private Long id;
    private String image;
    private String name;
    private Integer flagNum;
    private Double rating;
    private Integer reviewNum;
    private List<MapSimpleReviewDto> simpleReviewList;

    @Builder
    public FlagBakeryCardDto(Bakery bakery, Double rating, Integer reviewNum, List<MapSimpleReviewDto> simpleReviewList) {
        this.id = bakery.getId();
        this.image = bakery.getImage();
        this.name = bakery.getName();
        this.flagNum = bakery.getFlagNum();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.simpleReviewList = simpleReviewList;
    }
}
