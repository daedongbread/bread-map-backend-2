package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBakery(Bakery bakery);
    @Query(value = "SELECT r FROM Review r WHERE r.bakery = :bakery AND r.status = 'UNBLOCK'")
    Slice<Review> findSliceByBakeryOrder(@Param("bakery")Bakery bakery, Pageable pageable);
    @Query(value = "SELECT r FROM Review r JOIN r.ratings pr WHERE r.bakery = :bakery AND r.status = 'UNBLOCK' ORDER BY AVG(pr.rating) DESC, r.createdAt DESC")
    Slice<Review> findSliceByBakeryOrderByRatingDesc(@Param("bakery")Bakery bakery, Pageable pageable);
    @Query(value = "SELECT r FROM Review r JOIN r.ratings pr WHERE r.bakery = :bakery AND r.status = 'UNBLOCK' ORDER BY AVG(pr.rating) ASC, r.createdAt DESC")
    Slice<Review> findSliceByBakeryOrderByRatingAsc(@Param("bakery")Bakery bakery, Pageable pageable);
    Long countByUserId(Long userId);
    List<Review> findByUser(User user);
    void deleteByUser(User user);
    Optional<Review> findByIdAndUser(Long id, User user);
    Integer countByUser(User user);
}
