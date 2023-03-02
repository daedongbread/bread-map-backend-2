package com.depromeet.breadmapbackend.web.controller.flag.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlagBakeryDto {
    private FlagInfo flagInfo;
    private List<FlagBakeryInfo> flagBakeryInfoList;

    @Getter
    @NoArgsConstructor
    public static class FlagBakeryInfo {
        private Long id;
        private String image;
        private String name;
        private Integer flagNum;
        private Double rating;
        private Integer reviewNum;
        private List<MapSimpleReviewDto> simpleReviewList;

        @Builder
        public FlagBakeryInfo(Bakery bakery, Double rating, Integer reviewNum, List<MapSimpleReviewDto> simpleReviewList) {
            this.id = bakery.getId();
            this.image = bakery.getImage();
            this.name = bakery.getName();
            this.flagNum = bakery.getFlagNum();
            this.rating = rating;
            this.reviewNum = reviewNum;
            this.simpleReviewList = simpleReviewList;
        }
    }

    @Builder
    public FlagBakeryDto(Flag flag, List<FlagBakeryInfo> flagBakeryInfoList) {
        this.flagInfo = FlagInfo.builder().flag(flag).build();
        this.flagBakeryInfoList = flagBakeryInfoList;
    }
}
