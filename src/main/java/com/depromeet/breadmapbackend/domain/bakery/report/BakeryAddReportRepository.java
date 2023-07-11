package com.depromeet.breadmapbackend.domain.bakery.report;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BakeryAddReportRepository extends JpaRepository<BakeryAddReport, Long> {
	@Query(value = "select b from BakeryAddReport b", countQuery = "select count(b) from BakeryAddReport b")
	Page<BakeryAddReport> findPageAll(Pageable pageable);

	Long countByStatus(BakeryAddReportStatus status);

	@Query("select br "
		+ "from BakeryAddReport br "
		+ "join fetch br.user u "
		+ "where br.id = :reportId")
	Optional<BakeryAddReport> findBakeryReportWithPioneerById(@Param("reportId") Long reportId);
}
