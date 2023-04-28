package com.depromeet.breadmapbackend.domain.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
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
    private int flagNum;
    private Double rating;
    private int reviewNum;
    private List<MapSimpleReviewDto> simpleReviewList;
    private Double distance;
    private int popularNum;
    private FlagColor color;

    @Builder
    public BakeryCardDto(Bakery bakery, int flagNum, Double rating, Integer reviewNum, List<MapSimpleReviewDto> simpleReviewList, Double distance, FlagColor color) {
        this.latitude = bakery.getLatitude();
        this.longitude = bakery.getLongitude();
        this.id = bakery.getId();
        this.image = bakery.getImage();
        this.name = bakery.getName();
        this.flagNum = flagNum;
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.simpleReviewList = simpleReviewList;
        this.distance = distance;
        this.popularNum = flagNum + reviewNum;
        this.color = color;
    }
}
