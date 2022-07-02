package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
<<<<<<< HEAD
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportDto;
=======
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14

import java.util.List;

public interface AdminService {
    List<AdminAllBakeryDto> getAllBakeryList();
    AdminBakeryDto getBakeryDetail(Long bakeryId);
    void addBakery(String username, AddBakeryRequest request);
<<<<<<< HEAD
    List<BakeryReportDto> getAllBakeryReport();
    BakeryReportDto getBakeryReportDetail(Long reportId);
    void updateBakeryReport(Long reportId, UpdateBakeryReportStatusRequest request);
=======
    //List<BakeryReportDto> getAllBakeryReport();
    //BakeryReportDto getBakeryReportDetail(Long reportId);
    //void updateBakeryReport(Long reportId, UpdateBakeryReportStatusRequest request);
>>>>>>> 6dd3eff173e00ee5d4cd6bf4c3385e8785432c14
}
