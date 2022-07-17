package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportDto;

import java.util.List;

public interface AdminService {
    List<AdminAllBakeryDto> getAllBakeryList();
    AdminBakeryDto getBakeryDetail(Long bakeryId);
    void addBakery(String username, AddBakeryRequest request);
    List<BakeryReportDto> getAllBakeryReport();
    BakeryReportDto getBakeryReportDetail(Long reportId);
    void updateBakeryReport(Long reportId, UpdateBakeryReportStatusRequest request);
}
