package com.depromeet.breadmapbackend.domain.report;

import com.depromeet.breadmapbackend.domain.report.dto.ReportCommand;
import com.depromeet.breadmapbackend.domain.report.dto.request.ReportRequest;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public class Mapper {

	public static ReportCommand of(
		final ReportRequest request,
		final Long contentId,
		final ReportType reportType
	) {
		return new ReportCommand(
			reportType,
			contentId,
			request.reason(),
			request.content()
		);
	}

}
