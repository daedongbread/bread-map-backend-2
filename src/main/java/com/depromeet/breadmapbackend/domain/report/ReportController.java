package com.depromeet.breadmapbackend.domain.report;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.report.dto.request.ReportRequest;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

/**
 * ReportController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/{reportType}/{contentId}")
	@ResponseStatus(HttpStatus.CREATED)
	void report(
		@PathVariable("reportType") final String reportType,
		@PathVariable("contentId") final Long contentId,
		@RequestBody @Validated(ValidationSequence.class) ReportRequest request,
		@AuthenticationPrincipal final CurrentUserInfo CurrentUserInfo
	) {
		final ReportType type = ReportType.of(reportType);
		reportService.report(
			Mapper.of(request, contentId, type),
			CurrentUserInfo.getId()
		);
	}
}
