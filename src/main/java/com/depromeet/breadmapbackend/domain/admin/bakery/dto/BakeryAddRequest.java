package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryAddRequest {
    @NotBlank(message = "빵집 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String name;

    private String image;

    @NotBlank(message = "주소는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=3, max=100, message = "3자 이상, 100자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String address;
    @NotNull(message = "위도는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Double latitude;
    @NotNull(message = "경도는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Double longitude;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;
    private List<FacilityInfo> facilityInfoList;

    @Valid
    private List<ProductAddRequest> productList;
    private BakeryStatus status;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAddRequest {
        private ProductType productType;
        @NotBlank(message = "빵 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private String productName;
        @NotBlank(message = "빵 가격은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
        private String price;
        private String image;
    }
}
