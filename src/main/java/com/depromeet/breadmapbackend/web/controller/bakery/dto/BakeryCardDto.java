package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryCardDto {
    private String image;
    private String name;
    private Integer flagNum;
    private Long rating;
    private Integer reviewNum;
//    private List<SimpleReviewDto> simpleReviewList;
}
