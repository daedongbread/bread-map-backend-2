package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryAddReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryReportImageRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryUpdateReportRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BakeryReportService {
    void bakeryAddReport(String oAuthId, BakeryAddReportRequest request);
    void bakeryUpdateReport(String oAuthId, Long bakeryId, BakeryUpdateReportRequest request);
    void bakeryReportImage(String oAuthId, Long bakeryId, BakeryReportImageRequest request);
}
