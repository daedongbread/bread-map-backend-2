package com.depromeet.breadmapbackend.web.controller.bakery.dto;


import com.depromeet.breadmapbackend.domain.bakery.Bread;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleBreadDto {
    private Long id;
    private String name;
    private Integer price;
    private String image;

    @Builder
    public SimpleBreadDto(Bread bread) {
        this.id = bread.getId();
        this.name = bread.getName();
        this.price = bread.getPrice();
        this.image = bread.getImage();
    }
}
