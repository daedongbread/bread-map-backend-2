package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bread;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBreadDto {
    private String name;
    private Integer price;
    private String image;

    @Builder
    public AdminBreadDto(Bread bread) {
        this.name = bread.getName();
        this.price = bread.getPrice();
        this.image = bread.getImage();
    }
}
