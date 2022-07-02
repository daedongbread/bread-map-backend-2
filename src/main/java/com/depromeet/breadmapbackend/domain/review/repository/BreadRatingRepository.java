package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BreadRatingRepository extends JpaRepository<BreadRating, Long> {
    @Query("SELECT AVG(br.rating) FROM BreadRating br WHERE br.bread.id = ?1")
    Double findBreadAvgRating(Long breadId);
    Integer countByBreadId(Long breadId);
    Optional<BreadRating> findByBreadAndReview(Bread bread, Review review);
}
