package com.depromeet.breadmapbackend.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ReportJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
public interface ReportJpaRepository extends JpaRepository<Report, Long> {

	@Modifying
	@Query("delete from Report r where r.postId = :postId and r.reportType = :reportType")
	void deleteByPostIdAndReportType(
		@Param("postId") Long postId,
		@Param("reportType") ReportType reportType
	);
}
