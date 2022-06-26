package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.BakeryReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryReportRepository extends JpaRepository<BakeryReport, Long> {
}
