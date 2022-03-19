package com.depromeet.breadmapbackend.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BakeryReviewRepository extends JpaRepository<BakeryReview, Long>, BakeryReviewRepositoryCustom {

}
