package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewRequest;
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
public class AddBakeryRequest {
    @NotBlank(message = "빵집 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String name;
    @NotBlank(message = "주소는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String address;
    @NotBlank(message = "위도는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Double latitude;
    @NotBlank(message = "경도는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Double longitude;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;
    private List<FacilityInfo> facilityInfoList;
    private List<AddProductRequest> productList;
//    @NotBlank(message = "게시상태는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private BakeryStatus status;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProductRequest {
        private ProductType productType;
        @NotBlank(message = "상품 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private String productName;
        @NotBlank(message = "가격은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private Integer price;
    }
}
