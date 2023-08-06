package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BakeryProductsDto {

	private Long productId;
	private String productType;
	private String productName;
	private String productPrice;

	@Builder
	public BakeryProductsDto(Long productId, String productType, String productName, String productPrice) {
		this.productId = productId;
		this.productType = productType;
		this.productName = productName;
		this.productPrice = productPrice;
	}

	public static BakeryProductsDto of(Product product) {
		return BakeryProductsDto.builder()
			.productId(product.getId())
			.productType(product.getProductType().toString())
			.productName(product.getName())
			.productPrice(product.getPrice())
			.build();
	}
}
