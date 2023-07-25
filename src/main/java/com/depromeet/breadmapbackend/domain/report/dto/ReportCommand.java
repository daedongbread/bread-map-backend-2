package com.depromeet.breadmapbackend.domain.report.dto;

import com.depromeet.breadmapbackend.domain.report.ReportReason;
import com.depromeet.breadmapbackend.domain.report.ReportType;

/**
 * ReportCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public record ReportCommand(
	ReportType reportType,
	Long contentId,
	ReportReason reason,
	String content
) {
}
