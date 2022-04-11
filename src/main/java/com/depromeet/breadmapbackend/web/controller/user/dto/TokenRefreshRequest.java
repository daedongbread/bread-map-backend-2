package com.depromeet.breadmapbackend.web.controller.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TokenRefreshRequest {

    private String refreshToken;

}