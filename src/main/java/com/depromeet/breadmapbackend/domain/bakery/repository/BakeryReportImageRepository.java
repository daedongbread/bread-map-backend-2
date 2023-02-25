package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryReportImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryReportImageRepository extends JpaRepository<BakeryReportImage, Long> {
    Page<BakeryReportImage> findPageByBakery(Bakery bakery, Pageable pageable);
    void deleteByBakery(Bakery bakery);
}
