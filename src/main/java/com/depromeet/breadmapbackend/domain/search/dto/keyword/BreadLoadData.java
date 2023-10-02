package com.depromeet.breadmapbackend.domain.search.dto.keyword;

import lombok.Getter;

@Getter
public class BreadLoadData extends CommonLoadData{
    private final Long breadId;
    private final String breadName;

    public BreadLoadData(Long breadId, String breadName, Long bakeryId, String bakeryName, String bakeryAddress, Double longitude, Double latitude, Double totalScore, Long reviewCount) {
        super(bakeryId, bakeryName, bakeryAddress, longitude, latitude, totalScore, reviewCount);
        this.breadName = breadName;
        this.breadId = breadId;
    }
}
