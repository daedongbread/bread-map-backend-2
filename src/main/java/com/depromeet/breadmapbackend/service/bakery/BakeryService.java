package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.SortType;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;

import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> findBakeryList(Double latitude, Double longitude, Double height, Double width, SortType sort);
    List<BakeryFilterCardDto> findBakeryListByFilter(String username, Double latitude, Double longitude, Double height, Double width, SortType sort);
    BakeryDto findBakery(Long bakeryId);
}
