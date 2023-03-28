package com.depromeet.breadmapbackend.domain.admin.report;

import com.depromeet.breadmapbackend.domain.admin.report.dto.AdminReviewReportDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminReportService {
    PageResponseDto<AdminReviewReportDto> getReviewReportList(Pageable pageable);
    void updateReviewStatus(Long reportId);
}
