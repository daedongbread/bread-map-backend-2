package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class BakeryCardDto {
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

    @Builder
    public BakeryCardDto(Bakery bakery, Double rating, Integer reviewNum, List<MapSimpleReviewDto> simpleReviewList, Double distance) {
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
    }
}
