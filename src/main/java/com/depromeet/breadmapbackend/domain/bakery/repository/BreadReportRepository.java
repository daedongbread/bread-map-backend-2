package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.BreadReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadReportRepository extends JpaRepository<BreadReport, Long> {
}
