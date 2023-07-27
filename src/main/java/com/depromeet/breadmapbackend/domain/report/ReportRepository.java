package com.depromeet.breadmapbackend.domain.report;

/**
 * ReportReporitory
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public interface ReportRepository {
	Report save(Report report);

	void deleteByPostIdAndReportType(Long postId, ReportType reportType);
}
