package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {
    AdminBakeryListDto getBakeryList(Pageable pageable);
    AdminBakeryDto getBakery(Long bakeryId);
    BakeryLocationDto getBakeryLatitudeLongitude(String address) throws JsonProcessingException;
//    BakeryLocationDto getBakeryLatitudeLongitude(String address);
    void addBakery(AddBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList);
    void updateBakery(Long bakeryId, UpdateBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList);
    BakeryAddReportListDto getBakeryReportList(Pageable pageable);
    BakeryAddReportDto getBakeryReport(Long reportId);
    void updateBakeryAddReportStatus(Long reportId, UpdateBakeryReportStatusRequest request);
    AdminReviewReportListDto getReviewReportList(Pageable pageable);
    void updateReviewStatus(Long reportId);
    AdminUserListDto getUserList(Pageable pageable);
    void changeUserBlock(Long userId);
}
