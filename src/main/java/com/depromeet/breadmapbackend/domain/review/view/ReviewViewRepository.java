package com.depromeet.breadmapbackend.domain.review.view;

import com.depromeet.breadmapbackend.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewViewRepository extends JpaRepository<ReviewView, Long> {
    Optional<ReviewView> findByReview(Review review);
    void deleteByReview(Review review);
}
