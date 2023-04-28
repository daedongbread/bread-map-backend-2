package com.depromeet.breadmapbackend.domain.admin.bakery.dto;


import com.depromeet.breadmapbackend.domain.bakery.Bakery;


import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminSimpleBakeryDto {
    private Long bakeryId;
    private String name;
    private Integer bakeryReportImageNum;
    private Integer productAddReportNum;
    private Integer bakeryUpdateReportNum;
    private Integer newReviewNum;
    private String createdAt;
    private String modifiedAt;
    private BakeryStatus status;

    @Builder
    public AdminSimpleBakeryDto(
            Bakery bakery,
            Integer bakeryReportImageNum,
            Integer productAddReportNum,
            Integer bakeryUpdateReportNum,
            Integer newReviewNum
    ) {
        this.bakeryId = bakery.getId();
        this.name = bakery.getName();
        this.bakeryReportImageNum = bakeryReportImageNum;
        this.productAddReportNum = productAddReportNum;
        this.bakeryUpdateReportNum = bakeryUpdateReportNum;
        this.newReviewNum = newReviewNum;
        this.createdAt = bakery.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.modifiedAt = bakery.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.status = bakery.getStatus();
    }
}
