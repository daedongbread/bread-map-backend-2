package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> getBakeryList(String username, BakerySortType sortBy, boolean filterBy, Double latitude, Double longitude, Double height, Double width);
    BakeryDto getBakery(String username, Long bakeryId);
}
