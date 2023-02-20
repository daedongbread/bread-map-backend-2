package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryReportImage;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminImageDto {
    private Long imageId;
    private String image;

    @Builder
    public AdminImageDto(ReviewImage reviewImage) {
        this.imageId = reviewImage.getId();
        this.image = reviewImage.getImage();
    }

    @Builder
    public AdminImageDto(ProductAddReportImage productAddReportImage) {
        this.imageId = productAddReportImage.getId();
        this.image = productAddReportImage.getImage();
    }

    @Builder
    public AdminImageDto(BakeryReportImage bakeryReportImage) {
        this.imageId = bakeryReportImage.getId();
        this.image = bakeryReportImage.getImage();
    }
}
