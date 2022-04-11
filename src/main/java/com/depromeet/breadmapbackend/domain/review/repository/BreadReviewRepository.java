package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.BreadReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuReviewRepository extends JpaRepository<BreadReview, Long>, MenuReviewRepositoryCustom {

}
