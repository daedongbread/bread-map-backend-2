package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationBakery;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationFeedResponseDto {

	private long bakeryId;
	private String bakeryName;
	private String bakeryAddress;
	private String openingHours;
	private String bakeryImageUrl;
	private String checkPoint;
	private String newBreadTime;
	private String address;
	private String detailedAddress;
	private String websiteURL;
	private String instagramURL;
	private String facebookURL;
	private String blogURL;
	private List<String> facilityInfo;
	private String phoneNumber;
	private boolean isFlagged;
	private String reason;

	private long productId;
	private String productName;
	private String productPrice;
	private String productImageUrl;

	@Builder
	public CurationFeedResponseDto(long bakeryId, String bakeryName, String bakeryAddress, String openingHours,
		String bakeryImageUrl, String checkPoint, String newBreadTime, String address, String detailedAddress,
		String websiteURL, String instagramURL, String facebookURL, String blogURL, List<String> facilityInfo,
		String phoneNumber, String reason, boolean isFlagged, long productId, String productName, String productPrice,
		String productImageUrl) {
		this.bakeryId = bakeryId;
		this.bakeryName = bakeryName;
		this.bakeryAddress = bakeryAddress;
		this.openingHours = openingHours;
		this.bakeryImageUrl = bakeryImageUrl;
		this.checkPoint = checkPoint;
		this.newBreadTime = newBreadTime;
		this.productId = productId;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productImageUrl = productImageUrl;
		this.address = address;
		this.detailedAddress = detailedAddress;
		this.websiteURL = websiteURL;
		this.facebookURL = facebookURL;
		this.instagramURL = instagramURL;
		this.blogURL = blogURL;
		this.facilityInfo = facilityInfo;
		this.phoneNumber = phoneNumber;
		this.isFlagged = isFlagged;
		this.reason = reason;
	}

	public static CurationFeedResponseDto of(Bakery bakery, Product product) {

		return CurationFeedResponseDto.builder()
			.bakeryId(bakery.getId())
			.bakeryName(bakery.getName())
			.bakeryAddress(bakery.getAddress())
			.openingHours(bakery.getHours())
			.bakeryImageUrl(bakery.getImage())
			.checkPoint(bakery.getCheckPoint())
			.newBreadTime(bakery.getNewBreadTime())
			.address(bakery.getAddress())
			.detailedAddress(bakery.getDetailedAddress())
			.facebookURL(bakery.getBakeryURL().getBlogURL())
			.instagramURL(bakery.getBakeryURL().getInstagramURL())
			.blogURL(bakery.getBakeryURL().getBlogURL())
			.websiteURL(bakery.getBakeryURL().getWebsiteURL())
			.facilityInfo(bakery.getFacilityInfoList().stream().map(FacilityInfo::getCode).collect(Collectors.toList()))
			.phoneNumber(bakery.getPhoneNumber())
			.productId(product.getId())
			.productName(product.getName())
			.productPrice(product.getPrice())
			.productImageUrl(product.getImage())
			.build();
	}

	public void setIsFlagged(FlagBakery flagBakery) {
		if (flagBakery.getBakery().getId() == this.bakeryId) {
			this.isFlagged = true;
		}
	}

	public void setRecommendReason(CurationBakery curationBakery) {
		if (curationBakery.getBakery().getId() == this.bakeryId) {
			this.reason = curationBakery.getReason();
		}
	}
}
