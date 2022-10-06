package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private List<ProductRatingRequest> productRatingList;
    private List<NoExistProductRatingRequest> noExistProductRatingRequestList;
    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String content;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRatingRequest {
        @NotBlank(message = "상품 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private Long productId;
        @NotBlank(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private Long rating;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoExistProductRatingRequest {
        private ProductType productType;
        @NotBlank(message = "상품 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private String productName;
        @NotBlank(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private Long rating;
    }
}
