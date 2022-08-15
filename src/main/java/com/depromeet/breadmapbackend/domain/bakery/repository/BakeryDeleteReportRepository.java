package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryDeleteReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryDeleteReportRepository extends JpaRepository<BakeryDeleteReport, Long> {
    void deleteByBakery(Bakery bakery);
}
