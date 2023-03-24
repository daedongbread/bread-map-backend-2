package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.domain.review.dto.ReviewReportRequest;

public interface ReviewReportService {
    void reviewReport(String username, Long reviewId, ReviewReportRequest request);
}
