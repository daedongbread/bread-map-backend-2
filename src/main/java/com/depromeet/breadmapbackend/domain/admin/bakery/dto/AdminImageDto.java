package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryReportImage;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminImageDto {
    private Long imageId;
    private String image;
    private Boolean isNew;

    @Builder
    public AdminImageDto(ReviewImage reviewImage) {
        this.imageId = reviewImage.getId();
        this.image = reviewImage.getImage();
        this.isNew = reviewImage.getIsNew();
    }

    @Builder
    public AdminImageDto(ProductAddReportImage productAddReportImage) {
        this.imageId = productAddReportImage.getId();
        this.image = productAddReportImage.getImage();
        this.isNew = productAddReportImage.getIsNew();
    }

    @Builder
    public AdminImageDto(BakeryReportImage bakeryReportImage) {
        this.imageId = bakeryReportImage.getId();
        this.image = bakeryReportImage.getImage();
        this.isNew = bakeryReportImage.getIsNew();
    }
}
