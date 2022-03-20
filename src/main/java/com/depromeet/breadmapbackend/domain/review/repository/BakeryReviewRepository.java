package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.BakeryReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryReviewRepository extends JpaRepository<BakeryReview, Long>, BakeryReviewRepositoryCustom {

}
