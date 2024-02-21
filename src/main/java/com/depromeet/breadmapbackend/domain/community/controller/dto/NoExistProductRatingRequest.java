package com.depromeet.breadmapbackend.domain.community.controller.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record NoExistProductRatingRequest(
    @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
    ProductType productType,
    @NotBlank(message = "상품 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    String productName,
    String price,
    @NotNull(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    Long rating
) {

}