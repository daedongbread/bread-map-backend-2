package com.depromeet.breadmapbackend.domain.admin.bakery.dto;


import com.depromeet.breadmapbackend.domain.bakery.Bakery;


import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminSimpleBakeryDto {
    private Long bakeryId;
    private String name;
    private String createdAt;
    private String modifiedAt;
    private BakeryStatus status;

    @Builder
    public AdminSimpleBakeryDto(Bakery bakery) {
        this.bakeryId = bakery.getId();
        this.name = bakery.getName();
        this.createdAt = bakery.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.modifiedAt = bakery.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.status = bakery.getStatus();
    }
}
