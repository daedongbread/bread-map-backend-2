package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AdminService {
    void adminJoin(AdminJoinRequest request);
    JwtToken adminLogin(AdminLoginRequest request);
    JwtToken reissue(ReissueRequest reissueRequest);
    AdminBakeryListDto getBakeryList(Pageable pageable);
    AdminBakeryDto getBakery(Long bakeryId);
    AdminBakeryListDto searchBakeryList(String name, Pageable pageable);
    BakeryLocationDto getBakeryLatitudeLongitude(String address) throws JsonProcessingException;
    void addBakery(AddBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) throws IOException;
    void updateBakery(Long bakeryId, UpdateBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) throws IOException;
    AdminBakeryReviewImageListDto getBakeryReviewImages(Long bakeryId, Pageable pageable);
    void deleteBakery(Long bakeryId);
    BakeryAddReportListDto getBakeryReportList(Pageable pageable);
    BakeryAddReportDto getBakeryReport(Long reportId);
    void updateBakeryAddReportStatus(Long reportId, UpdateBakeryReportStatusRequest request);
    AdminReviewReportListDto getReviewReportList(Pageable pageable);
    void updateReviewStatus(Long reportId);
    AdminUserListDto getUserList(Pageable pageable);
    void changeUserBlock(Long userId);
}
