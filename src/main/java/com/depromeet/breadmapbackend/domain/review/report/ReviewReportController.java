package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.domain.review.report.dto.ReviewReportRequest;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewReportController {
    private final ReviewReportService reviewReportService;

    @PostMapping("/{reviewId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewReport(
            @CurrentUser String username, @PathVariable Long reviewId,
            @RequestBody @Validated(ValidationSequence.class) ReviewReportRequest request) {
        reviewReportService.reviewReport(username, reviewId, request);
    }
}
