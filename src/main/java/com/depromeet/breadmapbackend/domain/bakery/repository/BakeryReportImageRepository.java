package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryReportImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryReportImageRepository extends JpaRepository<BakeryReportImage, Long> {
    Slice<BakeryReportImage> findSliceByBakery(Bakery bakery, Pageable pageable);
    Slice<BakeryReportImage> findSliceByBakeryAndIdLessThan(Bakery bakery, Long lastId, Pageable pageable);
    void deleteByBakery(Bakery bakery);
}
