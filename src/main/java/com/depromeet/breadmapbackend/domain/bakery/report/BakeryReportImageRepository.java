package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BakeryReportImageRepository extends JpaRepository<BakeryReportImage, Long> {
    long countByBakery(Bakery bakery);
    Page<BakeryReportImage> findPageByBakery(Bakery bakery, Pageable pageable);
    void deleteByBakery(Bakery bakery);
}
