package com.depromeet.breadmapbackend.domain.bakery.product.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAddReportImageRepository extends JpaRepository<ProductAddReportImage, Long> {
    boolean existsByBakeryAndIsNewIsTrue(Bakery bakery);
    long countByBakeryAndIsRegisteredIsTrue(Bakery bakery);
    Page<ProductAddReportImage> findPageByBakeryAndIsRegisteredIsTrue(Bakery bakery, Pageable pageable);
    Optional<ProductAddReportImage> findByImageAndProductAddReport(String image, ProductAddReport report);
    Optional<ProductAddReportImage> findByIdAndProductAddReport(Long id, ProductAddReport report);
}
