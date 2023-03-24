package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryAddReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryUpdateReportRequest;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/bakeries")
@RequiredArgsConstructor
public class BakeryReportController {
    private final BakeryReportService bakeryReportService;

    @PostMapping("/{bakeryId}/bakery-update-reports")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryUpdateReport(
            @CurrentUser String username, @PathVariable Long bakeryId,
            @RequestPart @Validated(ValidationSequence.class) BakeryUpdateReportRequest request,
            @RequestPart(required = false) List<MultipartFile> files) throws IOException {
        bakeryReportService.bakeryUpdateReport(username, bakeryId, request, files);
    }

    @PostMapping("/bakery-add-reports")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryAddReport(
            @CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) BakeryAddReportRequest request) {
        bakeryReportService.bakeryAddReport(username, request);
    }

    @PostMapping("/{bakeryId}/bakery-report-images")
    @ResponseStatus(HttpStatus.CREATED)
    public void bakeryReportImage(
            @CurrentUser String username, @PathVariable Long bakeryId, @RequestPart List<MultipartFile> files) throws IOException {
        bakeryReportService.bakeryReportImage(username, bakeryId, files);
    }
}
