package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBakery(Bakery bakery);
    Long countByUserId(Long userId);
    List<Review> findByUser(User user);
    void deleteByUser(User user);
    Optional<Review> findByIdAndUser(Long id, User user);
    Integer countByUser(User user);
}
