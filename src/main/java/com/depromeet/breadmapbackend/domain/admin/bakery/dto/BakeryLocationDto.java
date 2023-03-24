package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BakeryLocationDto {
    private Double latitude;
    private Double longitude;

    @Builder
    public BakeryLocationDto(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
