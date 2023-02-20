package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.common.SliceResponseDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AdminService {
    void adminJoin(AdminJoinRequest request);
    JwtToken adminLogin(AdminLoginRequest request);
    JwtToken reissue(ReissueRequest reissueRequest);
    AdminBarDto getAdminBar();
    PageResponseDto<AdminSimpleBakeryDto> getBakeryList(Pageable pageable);
    AdminBakeryDto getBakery(Long bakeryId);
    PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, Pageable pageable);
    BakeryLocationDto getBakeryLatitudeLongitude(String address);
    void addBakery(BakeryAddRequest request, MultipartFile bakeryImage, List<MultipartFile> productImageList) throws IOException;
    void updateBakery(Long bakeryId, BakeryUpdateRequest request, MultipartFile bakeryImage, List<MultipartFile> productImageList) throws IOException;
    void deleteProduct(Long bakeryId, Long productId);
    PageResponseDto<AdminImageDto> getAdminBakeryImages(Long bakeryId, int page, AdminBakeryImageType type);
    void deleteBakery(Long bakeryId);
    PageResponseDto<SimpleBakeryAddReportDto> getBakeryReportList(Pageable pageable);
    BakeryAddReportDto getBakeryReport(Long reportId);
    void updateBakeryAddReportStatus(Long reportId, BakeryReportStatusUpdateRequest request);
    PageResponseDto<AdminReviewReportDto> getReviewReportList(Pageable pageable);
    void updateReviewStatus(Long reportId);
    PageResponseDto<AdminUserDto> getUserList(Pageable pageable);
    void changeUserBlock(Long userId);
}
