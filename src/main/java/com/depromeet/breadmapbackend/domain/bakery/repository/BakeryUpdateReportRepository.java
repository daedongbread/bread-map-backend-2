package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryURL;
import com.depromeet.breadmapbackend.domain.bakery.BakeryUpdateReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BakeryUpdateReportRepository extends JpaRepository<BakeryUpdateReport, Long> {
    Page<BakeryUpdateReport> findPageByBakery(Bakery bakery, Pageable pageable);
    Optional<BakeryUpdateReport> findByIdAndBakery(Long id, Bakery bakery);
    void deleteByIdAndBakery(Long id, Bakery bakery);
    void deleteByBakery(Bakery bakery);
}
