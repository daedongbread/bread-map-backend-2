package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BreadRatingRepositroy extends JpaRepository<BreadRating, Long> {
    @Query("SELECT AVG(br.rating) FROM BreadRating br WHERE br.bread.id = ?1")
    Double findBreadAvgRating(Long breadId);
    Integer countByBreadId(Long breadId);
}
