package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryImageType;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

import java.util.List;

public interface AdminBakeryService {
    AdminBakeryAlarmBar getBakeryAlarmBar();
    PageResponseDto<AdminSimpleBakeryDto> getBakeryList(List<AdminBakeryFilter> filterBy, String name, int page);
    AdminBakeryDto getBakery(Long bakeryId);
//    PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page);
    BakeryLocationDto getBakeryLatitudeLongitude(String address);
    BakeryAddDto addBakery(BakeryAddRequest request);
    void updateBakery(Long bakeryId, BakeryUpdateRequest request);
    void deleteProduct(Long bakeryId, Long productId);
    AdminBakeryIsNewDto getAdminBakeryIsNewBar(Long bakeryId);
    AdminImageBarDto getAdminImageBar(Long bakeryId);
    PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType imageType, int page);
    void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId);
    PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page);
    void registerProductAddImage(Long bakeryId, Long reportId, AdminImageRegisterRequest request);
    void deleteProductAddReport(Long bakeryId, Long reportId);
    PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page);
    void changeBakeryUpdateReport(Long bakeryId, Long reportId);
    void deleteBakeryUpdateReport(Long bakeryId, Long reportId);
    PageResponseDto<NewReviewDto> getNewReviews(Long bakeryId, int page);
    void hideNewReview(Long bakeryId, Long reviewId);
    void registerNewReviewImage(Long bakeryId, Long reviewId, AdminImageRegisterRequest request);
    void deleteReview(Long bakeryId, Long reviewId);
//    void deleteBakery(Long bakeryId);
}
