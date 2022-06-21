package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBakeryId(Long bakeryId);
    Long countByUserId(Long userId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByIdAndIsUseIsTrue(Long id);
    Optional<Review> findByIdAndUserAndIsUseIsTrue(Long id, User user);
}
