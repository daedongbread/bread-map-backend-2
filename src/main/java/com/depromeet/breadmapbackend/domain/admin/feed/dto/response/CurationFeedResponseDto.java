package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationBakery;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;

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

	private long productId;
	private String productName;
	private String productPrice;
	private String productImageUrl;

	@Builder
	public CurationFeedResponseDto(long bakeryId, String bakeryName, String bakeryAddress, String openingHours,
		String bakeryImageUrl, String checkPoint, String newBreadTime, long productId, String productName,
		String productPrice,
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
	}

	public static CurationFeedResponseDto of(CurationBakery curationBakery) {

		Bakery bakery = curationBakery.getBakery();
		Product product = curationBakery.getBakery().getProduct(curationBakery.getProductId());

		return CurationFeedResponseDto.builder()
			.bakeryId(bakery.getId())
			.bakeryName(bakery.getName())
			.bakeryAddress(bakery.getAddress())
			.openingHours(bakery.getHours())
			.bakeryImageUrl(bakery.getImage())
			// .checkPoint(bakery.getCheckPoint()) // 추가되어야 할 컬럼
			// .newBreadTime() // 추가되어야 할 컬럼
			.productId(product.getId())
			.productName(product.getName())
			.productPrice(product.getPrice())
			.productImageUrl(product.getImage())
			.build();
	}
}
