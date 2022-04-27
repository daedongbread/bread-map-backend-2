package com.depromeet.breadmapbackend.security.token;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtToken {
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiredDate;

    @Builder
    public JwtToken(String accessToken, String refreshToken, Long accessTokenExpiredDate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiredDate = accessTokenExpiredDate;
    }
}