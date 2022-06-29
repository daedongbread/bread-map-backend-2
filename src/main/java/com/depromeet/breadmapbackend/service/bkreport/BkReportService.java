package com.depromeet.breadmapbackend.service.bkreport;

import com.depromeet.breadmapbackend.web.controller.bkreport.dto.AddBakeryReportRequest;

public interface BkReportService {
    void addBakeryReport(String username, AddBakeryReportRequest request);
}
