package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.domain.review.report.dto.ReviewReportRequest;

public interface ReviewReportService {
    void reviewReport(String username, Long reviewId, ReviewReportRequest request);
}
