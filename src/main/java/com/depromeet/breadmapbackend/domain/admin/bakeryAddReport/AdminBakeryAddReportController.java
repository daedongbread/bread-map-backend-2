package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryReportStatusUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.BakeryAddReportDto;
import com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto.SimpleBakeryAddReportDto;
import com.depromeet.breadmapbackend.global.converter.PageableSortConverter;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/bakery-add-reports")
@RequiredArgsConstructor
public class AdminBakeryAddReportController {
    private final AdminBakeryAddReportService adminBakeryAddReportService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<SimpleBakeryAddReportDto>> getBakeryAddReportList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminBakeryAddReportService.getBakeryAddReportList(PageableSortConverter.convertSort(pageable)));
    }

    @GetMapping("/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BakeryAddReportDto> getBakeryAddReport(@PathVariable Long reportId) {
        return new ApiResponse<>(adminBakeryAddReportService.getBakeryAddReport(reportId));
    }

    @PatchMapping("/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBakeryAddReportStatus(
            @PathVariable Long reportId, @RequestBody @Validated(ValidationSequence.class) BakeryReportStatusUpdateRequest request) {
        adminBakeryAddReportService.updateBakeryAddReportStatus(reportId, request);
    }
}
