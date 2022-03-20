package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.MenuReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuReviewRepository extends JpaRepository<MenuReview, Long>, MenuReviewRepositoryCustom {

}
