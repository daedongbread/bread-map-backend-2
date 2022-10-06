package com.depromeet.breadmapbackend.domain.product.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAddReportRepository extends JpaRepository<ProductAddReport, Long> {
    void deleteByBakery(Bakery bakery);
}
