package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryReportStatusUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.BakeryAddReportDto;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.SimpleBakeryAddReportDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminBakeryAddReportService {
    PageResponseDto<SimpleBakeryAddReportDto> getBakeryAddReportList(int page);
    BakeryAddReportDto getBakeryAddReport(Long reportId);
    void updateBakeryAddReportStatus(Long reportId, BakeryReportStatusUpdateRequest request);
}
