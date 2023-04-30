package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    boolean existsByBakeryAndIsNewIsTrue(Bakery bakery);
    Integer countByBakeryAndIsRegisteredIsTrue(Bakery bakery);
    Page<ReviewImage> findPageByBakeryAndIsRegisteredIsTrue(Bakery bakery, Pageable pageable);
    Optional<ReviewImage> findByIdAndBakery(Long id, Bakery bakery);
    Optional<ReviewImage> findByIdAndReview(Long id, Review review);
    void deleteByBakery(Bakery bakery);
    void deleteByReview(Review review);
}
