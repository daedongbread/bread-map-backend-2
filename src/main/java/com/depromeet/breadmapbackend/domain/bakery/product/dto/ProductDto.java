package com.depromeet.breadmapbackend.domain.bakery.product.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private ProductType productType;
    private String name;
    private Double rating;
    private Integer reviewNum;
    private String price;
    private String image;

    @Builder
    public ProductDto(Product product, Double rating, Integer reviewNum) {
        this.id = product.getId();
        this.productType = product.getProductType();
        this.name = product.getName();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.price = product.getPrice();
        this.image = product.getImage();
    }
}
