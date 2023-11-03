package com.depromeet.breadmapbackend.domain.search.dto.keyword;

import lombok.Getter;

@Getter
public class BakeryLoadData extends CommonLoadData{
    public BakeryLoadData(Long bakeryId, String bakeryName, String bakeryAddress, Double longitude, Double latitude) {
        super(bakeryId, bakeryName, bakeryAddress, longitude, latitude);
    }
}
