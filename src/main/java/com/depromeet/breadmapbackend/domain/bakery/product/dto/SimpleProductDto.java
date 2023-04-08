package com.depromeet.breadmapbackend.domain.bakery.product.dto;


import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleProductDto {
    private Long id;
    private ProductType productType;
    private String name;
    private String price;
    private String image;

    @Builder
    public SimpleProductDto(Product product) {
        this.id = product.getId();
        this.productType = product.getProductType();
        this.name = product.getName();
        this.price = product.getPrice();
        this.image = product.getImage();
    }
}
