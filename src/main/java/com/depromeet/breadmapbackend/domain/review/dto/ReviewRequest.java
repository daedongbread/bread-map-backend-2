package com.depromeet.breadmapbackend.domain.review.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private List<ProductRatingRequest> productRatingList;
    private List<NoExistProductRatingRequest> noExistProductRatingRequestList;
    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요", groups = ValidationGroups.SizeCheckGroup.class)
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
