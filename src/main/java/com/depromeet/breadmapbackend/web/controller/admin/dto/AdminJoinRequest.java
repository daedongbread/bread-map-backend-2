package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminJoinRequest {
    private String adminId;
    private String password;
    private String secret;
}
