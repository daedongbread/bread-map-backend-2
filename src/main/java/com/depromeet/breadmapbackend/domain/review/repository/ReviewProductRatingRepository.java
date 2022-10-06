package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewProductRatingRepository extends JpaRepository<ReviewProductRating, Long> {
    @Query("SELECT AVG(rpr.rating) FROM ReviewProductRating rpr WHERE rpr.product.id = ?1")
    Double findProductAvgRating(Long productId);
    Integer countByProductId(Long productId);
    Optional<ReviewProductRating> findByProductAndReview(Product product, Review review);
}
