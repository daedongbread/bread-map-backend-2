package com.depromeet.breadmapbackend.domain.bakery.product.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAddReportRepository extends JpaRepository<ProductAddReport, Long> {
    Integer countByBakeryAndIsNewIsTrue(Bakery bakery);
    Integer countByIsNewIsTrue();
    boolean existsByBakeryAndIsNewIsTrue(Bakery bakery);
    Page<ProductAddReport> findPageByBakery(Bakery bakery, Pageable pageable);
    Optional<ProductAddReport> findByIdAndBakery(Long id, Bakery bakery);
    void deleteByBakery(Bakery bakery);
}
