package com.depromeet.breadmapbackend.global.infra.feign.sgis.dto;

import lombok.Getter;

@Getter
public class SgisTranscoordDto {
    private SgisPosDto result;

    @Getter
    public class SgisPosDto {
        private Double posY;
        private Double posX;
    }
}
