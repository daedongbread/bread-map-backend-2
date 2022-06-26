package com.depromeet.breadmapbackend.web.controller.admin.dto;


import com.depromeet.breadmapbackend.domain.bakery.Bakery;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminAllBakeryDto {

    private long id;
    private boolean isUse;
    private String name;

    @Builder
    public AdminAllBakeryDto(Bakery bakery) {
        this.id = bakery.getId();
        this.isUse = bakery.isUse();
        this.name = bakery.getName();
    }
}
