package com.depromeet.breadmapbackend.global.infra.feign.sgis.dto;

import lombok.Getter;

@Getter
public class SgisTokenDto {
    private SgisAccessTokenDto result;

    @Getter
    public class SgisAccessTokenDto {
        private String accessToken;
        private String accessTimeout;
    }
}
