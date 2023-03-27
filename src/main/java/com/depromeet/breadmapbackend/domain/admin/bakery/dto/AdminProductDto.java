package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProductDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private String image;

    @Builder
    public AdminProductDto(Product product) {
        this.productId = product.getId();
        this.productType = product.getProductType();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.image = product.getImage();
    }
}
