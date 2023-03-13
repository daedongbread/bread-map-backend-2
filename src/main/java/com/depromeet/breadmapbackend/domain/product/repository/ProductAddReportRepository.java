package com.depromeet.breadmapbackend.domain.product.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAddReportRepository extends JpaRepository<ProductAddReport, Long> {
    Page<ProductAddReport> findPageByBakery(Bakery bakery, Pageable pageable);

    Optional<ProductAddReport> findByIdAndBakery(Long id, Bakery bakery);
    void deleteByIdAndBakery(Long id, Bakery bakery);
    void deleteByBakery(Bakery bakery);
}
