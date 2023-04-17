package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BakeryReportImageRepository extends JpaRepository<BakeryReportImage, Long> {
    boolean existsByBakeryAndIsNewIsTrue(Bakery bakery);
    Integer countByBakery(Bakery bakery);
    Integer countByBakeryAndIsNewIsTrue(Bakery bakery);
    Integer countByIsNewIsTrue();
    Page<BakeryReportImage> findPageByBakery(Bakery bakery, Pageable pageable);
    void deleteByBakery(Bakery bakery);
}
