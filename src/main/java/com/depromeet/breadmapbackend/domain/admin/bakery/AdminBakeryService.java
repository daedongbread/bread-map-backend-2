package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface AdminBakeryService {
    PageResponseDto<AdminSimpleBakeryDto> getBakeryList(int page);
    AdminBakeryDto getBakery(Long bakeryId);
    PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page);
    BakeryLocationDto getBakeryLatitudeLongitude(String address);
    void addBakery(BakeryAddRequest request);
    void updateBakery(Long bakeryId, BakeryUpdateRequest request);
    void deleteProduct(Long bakeryId, Long productId);

    AdminImageBarDto getAdminImageBar(Long bakeryId);
    PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType imageType, int page);
    void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId);
    PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page);
    void registerProductAddImage(Long bakeryId, Long reportId, ProductAddImageRegisterRequest request);
    void deleteProductAddReport(Long bakeryId, Long reportId);
    PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page);
    void changeBakeryUpdateReport(Long bakeryId, Long reportId);
    void deleteBakeryUpdateReport(Long bakeryId, Long reportId);
    void deleteBakery(Long bakeryId);
}
