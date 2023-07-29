package com.depromeet.breadmapbackend.domain.report;

import com.depromeet.breadmapbackend.domain.report.dto.ReportCommand;

/**
 * ReportService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public interface ReportService {
	void report(ReportCommand command, Long userId);
}
