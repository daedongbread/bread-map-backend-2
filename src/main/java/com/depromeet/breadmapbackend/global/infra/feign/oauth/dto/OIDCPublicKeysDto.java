package com.depromeet.breadmapbackend.global.infra.feign.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OIDCPublicKeysDto {
    List<OIDCPublicKeyDto> keys;

    @Getter
    @NoArgsConstructor
    public static class OIDCPublicKeyDto {
        private String kid;
        private String n;
        private String e;
    }
}
