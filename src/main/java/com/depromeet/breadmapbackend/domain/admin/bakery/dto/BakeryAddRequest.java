package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryAddRequest {
	@NotBlank(message = "빵집 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	@Size(min = 1, max = 20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
	private String name;

	@Size(max = 2)
	private List<String> images;

	@NotBlank(message = "주소는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	@Size(min = 3, max = 100, message = "3자 이상, 100자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
	private String address;
	private String detailedAddress;
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
	private String checkPoint;
	private String newBreadTime;
	private List<@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class) FacilityInfo> facilityInfoList;
	// private Long ownerId;

	@Valid
	private List<ProductAddRequest> productList;
	private BakeryStatus status;
	@NotNull(message = "빵집 제보 id는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private Long reportId;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProductAddRequest {
		@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
		private ProductType productType;
		@NotBlank(message = "빵 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
		private String productName;
		@NotBlank(message = "빵 가격은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
		private String price;
		private String image;
	}
}
