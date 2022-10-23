package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProductDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private String price;
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
