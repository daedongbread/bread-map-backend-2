package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.BreadRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadRatingRepositroy extends JpaRepository<BreadRating, Long> {
}
