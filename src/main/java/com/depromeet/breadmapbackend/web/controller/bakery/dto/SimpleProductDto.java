package com.depromeet.breadmapbackend.web.controller.bakery.dto;


import com.depromeet.breadmapbackend.domain.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleProductDto {
    private Long id;
    private String name;
    private Integer price;
    private String image;

    @Builder
    public SimpleProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.image = product.getImage();
    }
}
