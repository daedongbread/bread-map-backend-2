package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminBakeryListDto {
    private List<AdminSimpleBakeryDto> bakeryList;
    private int totalNum;

    @Builder
    public AdminBakeryListDto(List<AdminSimpleBakeryDto> bakeryDtoList, int totalNum) {
        this.bakeryList = bakeryDtoList;
        this.totalNum = totalNum;
    }
}
