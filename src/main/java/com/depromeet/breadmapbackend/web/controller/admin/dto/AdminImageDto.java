package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
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
    private AdminBakeryImageType type;
    private Long imageId;
    private String image;

    @Builder
    public AdminImageDto(ReviewImage reviewImage) {
        this.type = AdminBakeryImageType.REVIEW;
        this.imageId = reviewImage.getId();
        this.image = reviewImage.getImage();
    }

    @Builder
    public AdminImageDto(ProductAddReportImage productAddReportImage) {
        this.type = AdminBakeryImageType.PRODUCT;
        this.imageId = productAddReportImage.getId();
        this.image = productAddReportImage.getImage();
    }

    @Builder
    public AdminImageDto(BakeryReportImage bakeryReportImage) {
        this.type = AdminBakeryImageType.BAKERY;
        this.imageId = bakeryReportImage.getId();
        this.image = bakeryReportImage.getImage();
    }
}
