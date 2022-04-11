package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import lombok.*;

import java.util.List;

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
