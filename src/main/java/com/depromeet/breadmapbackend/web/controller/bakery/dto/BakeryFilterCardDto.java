package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BakeryFilterCardDto {
    private Double latitude;
    private Double longitude;

    private Long id;
    private String image;
    private String name;
    private Integer flagNum;
    private Double rating;
    private Integer reviewNum;
    private List<MapSimpleReviewDto> simpleReviewList;
    private Double distance;
    private Integer popularNum;

    private FlagColor color;

    @Builder
    public BakeryFilterCardDto(
            Bakery bakery, Double rating, Integer reviewNum, List<MapSimpleReviewDto> simpleReviewList, Double distance, FlagColor color) {
        this.latitude = bakery.getLatitude();
        this.longitude = bakery.getLongitude();
        this.id = bakery.getId();
        this.image = bakery.getImage();
        this.name = bakery.getName();
        this.flagNum = bakery.getFlagNum();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.simpleReviewList = simpleReviewList;
        this.distance = distance;
        this.popularNum = flagNum + reviewNum;
        this.color = color;
    }
}
