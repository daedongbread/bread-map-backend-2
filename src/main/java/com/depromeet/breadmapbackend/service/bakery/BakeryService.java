package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;

import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> getBakeryList(Double latitude, Double longitude, Double height, Double width);
}
