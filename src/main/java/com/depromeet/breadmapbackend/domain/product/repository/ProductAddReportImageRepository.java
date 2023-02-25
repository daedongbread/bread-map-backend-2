package com.depromeet.breadmapbackend.domain.product.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAddReportImageRepository extends JpaRepository<ProductAddReportImage, Long> {
    Page<ProductAddReportImage> findPageByBakery(Bakery bakery, Pageable pageable);
}
