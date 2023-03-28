package com.depromeet.breadmapbackend.domain.bakery.product.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private Double rating;
    private Integer reviewNum;
    private Integer price;
    private String image;

    @Builder
    public ProductDto(Product product, Double rating, Integer reviewNum) {
        this.id = product.getId();
        this.name = product.getName();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.price = product.getPrice();
        this.image = product.getImage();
    }
}
