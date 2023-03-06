package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
//    @Query(value = "select * from review_image", nativeQuery = true)
    Page<ReviewImage> findPageByBakery(Bakery bakery, Pageable pageable);
    void deleteByBakery(Bakery bakery);
    void deleteByReview(Review review);
}
