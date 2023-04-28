package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryReportStatusUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.BakeryAddReportDto;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.SimpleBakeryAddReportDto;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBakeryAddReportServiceImpl implements AdminBakeryAddReportService {
    private final BakeryAddReportRepository bakeryAddReportRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<SimpleBakeryAddReportDto> getBakeryAddReportList(int page) {
        PageRequest pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<BakeryAddReport> all = bakeryAddReportRepository.findPageAll(pageable);
        return PageResponseDto.of(all, SimpleBakeryAddReportDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryAddReportDto getBakeryAddReport(Long reportId) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_REPORT_NOT_FOUND));
        return BakeryAddReportDto.builder().bakeryAddReport(bakeryAddReport).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBakeryAddReportStatus(Long reportId, BakeryReportStatusUpdateRequest request) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_REPORT_NOT_FOUND));
        bakeryAddReport.updateStatus(request.getStatus());
    }
}
