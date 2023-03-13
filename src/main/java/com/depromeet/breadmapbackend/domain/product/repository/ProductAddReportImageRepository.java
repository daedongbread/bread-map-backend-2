package com.depromeet.breadmapbackend.domain.product.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAddReportImageRepository extends JpaRepository<ProductAddReportImage, Long> {
    Page<ProductAddReportImage> findPageByBakeryAndIsRegisteredIsTrue(Bakery bakery, Pageable pageable);
    Optional<ProductAddReportImage> findByImageAndProductAddReport(String image, ProductAddReport report);
    Optional<ProductAddReportImage> findByIdAndProductAddReport(Long id, ProductAddReport report);
}
