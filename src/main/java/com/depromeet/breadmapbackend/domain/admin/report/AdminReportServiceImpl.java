package com.depromeet.breadmapbackend.domain.admin.report;

import com.depromeet.breadmapbackend.domain.admin.report.dto.AdminReviewReportDto;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
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
public class AdminReportServiceImpl implements AdminReportService {
    private final ReviewReportRepository reviewReportRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminReviewReportDto> getReviewReportList(int page) {
        PageRequest pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<ReviewReport> all = reviewReportRepository.findPageAll(pageable);
        return PageResponseDto.of(all, AdminReviewReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateReviewStatus(Long reportId) {
        ReviewReport reviewReport = reviewReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_REPORT_NOT_FOUND));
        reviewReport.getReview().changeBlock();
        reviewReport.changeBlock();
    }
}
