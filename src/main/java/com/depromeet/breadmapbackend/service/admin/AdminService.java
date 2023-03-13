package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AdminService {
    void adminJoin(AdminJoinRequest request);
    JwtToken adminLogin(AdminLoginRequest request);
    JwtToken reissue(ReissueRequest reissueRequest);
    AdminBarDto getAdminBar();
    PageResponseDto<AdminSimpleBakeryDto> getBakeryList(int page);
    AdminBakeryDto getBakery(Long bakeryId);
    PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page);
    BakeryLocationDto getBakeryLatitudeLongitude(String address);
    void addBakery(BakeryAddRequest request);
    void updateBakery(Long bakeryId, BakeryUpdateRequest request);
    void deleteProduct(Long bakeryId, Long productId);
    PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType imageType, int page);
    ResponseEntity<byte[]> downloadAdminImage(String image) throws IOException;
    void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId);
    PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page);
    void registerProductAddImage(Long bakeryId, Long reportId, ProductAddImageRegisterRequest request);
    void deleteProductAddReport(Long bakeryId, Long reportId);
    PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page);
    void changeBakeryUpdateReport(Long bakeryId, Long reportId);
    void deleteBakeryUpdateReport(Long bakeryId, Long reportId);
    void deleteBakery(Long bakeryId);
    PageResponseDto<SimpleBakeryAddReportDto> getBakeryAddReportList(Pageable pageable);
    BakeryAddReportDto getBakeryAddReport(Long reportId);
    void updateBakeryAddReportStatus(Long reportId, BakeryReportStatusUpdateRequest request);
    PageResponseDto<AdminReviewReportDto> getReviewReportList(Pageable pageable);
    void updateReviewStatus(Long reportId);
    PageResponseDto<AdminUserDto> getUserList(Pageable pageable);
    void changeUserBlock(Long userId);
    TempImageDto uploadTempImage(MultipartFile file) throws IOException;
}
