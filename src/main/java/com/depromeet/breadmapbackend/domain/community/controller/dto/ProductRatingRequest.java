package com.depromeet.breadmapbackend.domain.community.controller.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import javax.validation.constraints.NotNull;

public record ProductRatingRequest(

    @NotNull(message = "상품 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    Long productId,
    @NotNull(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    Long rating
) {

}

//{
//    @Valid
//    private List<ProductRatingRequest> productRatingList;
//    @Valid
//    private List<NoExistProductRatingRequest> noExistProductRatingRequestList;
//    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요", groups = ValidationGroups.SizeCheckGroup.class)
//    private String content;
//
//    private List<String> images;
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ProductRatingRequest {
//        @NotNull(message = "상품 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//        private Long productId;
//        @NotNull(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//        private Long rating;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class NoExistProductRatingRequest {
//        @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
//        private ProductType productType;
//        @NotBlank(message = "상품 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//        private String productName;
//        private String price;
//        @NotNull(message = "점수는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//        private Long rating;
//    }
//}
