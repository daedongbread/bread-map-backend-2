package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BakeryAddReportRepository extends JpaRepository<BakeryAddReport, Long> {
    @Query(value = "select * from bakery_add_report", countQuery = "select count(*) from bakery_add_report", nativeQuery = true)
    Page<BakeryAddReport> findPageAll(Pageable pageable);
    Long countByStatus(BakeryAddReportStatus status);
}
