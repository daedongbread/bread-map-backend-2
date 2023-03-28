package com.depromeet.breadmapbackend.domain.admin.report;

import com.depromeet.breadmapbackend.domain.admin.report.dto.AdminReviewReportDto;
import com.depromeet.breadmapbackend.global.converter.PageableSortConverter;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminReportController {
    private final AdminReportService adminReportService;

    @GetMapping("/review-reports")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminReviewReportDto>> getReviewReportList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminReportService.getReviewReportList(PageableSortConverter.convertSort(pageable)));
    }

    @PatchMapping("/review-reports/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReviewStatus(@PathVariable Long reportId) {
        adminReportService.updateReviewStatus(reportId);
    }
}
