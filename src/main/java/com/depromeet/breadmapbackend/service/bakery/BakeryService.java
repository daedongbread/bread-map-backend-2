package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> findBakeryList(Double latitude, Double longitude, Double height, Double width, BakerySortType sort);
    List<BakeryFilterCardDto> findBakeryListByFilter(String username, Double latitude, Double longitude, Double height, Double width, BakerySortType sort);
    BakeryDto findBakery(Long bakeryId);
    void bakeryUpdateReport(Long bakeryId, BakeryUpdateRequest request);
    void bakeryDeleteReport(Long bakeryId, MultipartFile file) throws IOException;
    void bakeryAddReport(BakeryReportRequest request);
    void breadAddReport(Long bakeryId, BreadReportRequest request, List<MultipartFile> files) throws IOException;
}
