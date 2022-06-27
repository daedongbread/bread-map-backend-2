package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryUpdateRequest {
    private String name;
    private String location;
    private String content;
}
