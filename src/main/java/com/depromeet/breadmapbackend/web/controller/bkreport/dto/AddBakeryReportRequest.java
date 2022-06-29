package com.depromeet.breadmapbackend.web.controller.bkreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBakeryReportRequest {
    private String bakeryName;
    private String address;
    private String reason;
}
