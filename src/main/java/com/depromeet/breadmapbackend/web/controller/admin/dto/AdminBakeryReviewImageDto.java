package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBakeryReviewImageDto {
    private Long imageId;
    private String image;

    @Builder
    public AdminBakeryReviewImageDto(ReviewImage reviewImage) {
        this.imageId = reviewImage.getId();
        this.image = reviewImage.getImage();
    }
}
