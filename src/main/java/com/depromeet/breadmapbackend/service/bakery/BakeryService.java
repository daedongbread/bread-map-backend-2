package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;

import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> findBakeryList(Double latitude, Double longitude, Double height, Double width, BakerySortType sort);
    List<BakeryFilterCardDto> findBakeryListByFilter(String username, Double latitude, Double longitude, Double height, Double width, BakerySortType sort);
    BakeryDto findBakery(Long bakeryId);
}
