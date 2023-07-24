package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import java.util.List;
import java.util.Optional;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBakeryDto {
	private String name;
	private Long pioneerId;
	private String pioneerNickName;
	private String image;

	private String address;
	private String detailedAddress;
	private Double longitude;
	private Double latitude;

	private String hours;
	private String websiteURL;
	private String instagramURL;
	private String facebookURL;
	private String blogURL;
	private String phoneNumber;
	private List<FacilityInfo> facilityInfoList;
	private BakeryStatus status;

	private List<AdminProductDto> productList;
	private Long reportId;

	@Builder
	public AdminBakeryDto(Bakery bakery, String image, List<AdminProductDto> productList) {
		final Optional<BakeryAddReport> bakeryAddReport = Optional.ofNullable(bakery.getBakeryAddReport());
		final User pioneer = bakeryAddReport.map(BakeryAddReport::getUser).orElse(null);
		this.name = bakery.getName();
		this.reportId = bakeryAddReport.map(BakeryAddReport::getId).orElse(null);
		this.pioneerId = (pioneer != null) ? pioneer.getId() : null;
		this.pioneerNickName = (pioneer != null) ? pioneer.getNickName() : null;
		this.image = image;
		this.address = bakery.getAddress();
		this.detailedAddress = bakery.getDetailedAddress();
		this.latitude = bakery.getLatitude();
		this.longitude = bakery.getLongitude();
		this.hours = bakery.getHours();
		this.websiteURL = bakery.getBakeryURL().getWebsiteURL();
		this.instagramURL = bakery.getBakeryURL().getInstagramURL();
		this.facebookURL = bakery.getBakeryURL().getFacebookURL();
		this.blogURL = bakery.getBakeryURL().getBlogURL();
		this.phoneNumber = bakery.getPhoneNumber();
		this.facilityInfoList = bakery.getFacilityInfoList();
		this.status = bakery.getStatus();
		this.productList = productList;
	}
}
