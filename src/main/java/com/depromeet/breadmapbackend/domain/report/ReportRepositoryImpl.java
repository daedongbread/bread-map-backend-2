package com.depromeet.breadmapbackend.domain.report;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * ReportRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
@RequiredArgsConstructor
@Repository
public class ReportRepositoryImpl implements ReportRepository {
	private final ReportJpaRepository reportJpaRepository;

	@Override
	public Report save(final Report report) {
		return reportJpaRepository.save(report);
	}

	@Override
	public void deleteByPostIdAndReportType(final Long postId, final ReportType reportType) {
		reportJpaRepository.deleteByPostIdAndReportType(postId, reportType);
	}
}
