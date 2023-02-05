package com.depromeet.breadmapbackend.security.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiredDate;
}