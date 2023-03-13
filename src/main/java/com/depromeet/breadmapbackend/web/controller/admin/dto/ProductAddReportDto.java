package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ProductAddReportDto {
    private Long reportId;
    private LocalDateTime createdAt;
    private String name;
    private String price;
    private String nickName;
    private List<ProductAddReportImageDto> imageList;


    @Getter
    @NoArgsConstructor
    public static class ProductAddReportImageDto {
        private Long imageId;
        private String image;
        private Boolean isRegistered;

        @Builder
        public ProductAddReportImageDto(ProductAddReportImage productAddReportImage) {
            this.imageId = productAddReportImage.getId();
            this.image = productAddReportImage.getImage();
            this.isRegistered = productAddReportImage.getIsRegistered();
        }
    }

    @Builder
    public ProductAddReportDto(ProductAddReport report) {
        this.reportId = report.getId();
        this.createdAt = report.getCreatedAt();
        this.name = report.getName();
        this.price = report.getPrice();
        this.nickName = report.getUser().getNickName();
        this.imageList = report.getImages().stream().map(ProductAddReportImageDto::new).collect(Collectors.toList());
    }
}
