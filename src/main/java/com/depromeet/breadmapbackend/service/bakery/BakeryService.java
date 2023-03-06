package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.BakerySortType;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BakeryService {
    List<BakeryCardDto> findBakeryList(Double latitude, Double longitude, Double height, Double width, BakerySortType sortBy);
    List<BakeryCardDto> findBakeryListByFilter(String username, Double latitude, Double longitude, Double height, Double width, BakerySortType sortBy);
    BakeryDto findBakery(String username, Long bakeryId);
    List<ProductDto> findProductList(Long bakeryId);
    void bakeryUpdateReport(String username, Long bakeryId, BakeryUpdateRequest request, List<MultipartFile> files) throws IOException;
    void bakeryAddReport(String username, BakeryReportRequest request);
    void bakeryReportImage(String username, Long bakeryId, List<MultipartFile> files) throws IOException;
    void productAddReport(String username, Long bakeryId, ProductReportRequest request, List<MultipartFile> files) throws IOException;
    List<SimpleProductDto> searchSimpleProductList(Long bakeryId, String name);
}
