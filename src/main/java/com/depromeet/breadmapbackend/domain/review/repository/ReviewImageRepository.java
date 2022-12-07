package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    Slice<ReviewImage> findSliceByBakery(Bakery bakery, Pageable pageable);
    void deleteByBakery(Bakery bakery);
    void deleteByReview(Review review);
}
