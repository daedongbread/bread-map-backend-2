package com.depromeet.breadmapbackend.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuReviewRepository extends JpaRepository<MenuReview, Long>, MenuReviewRepositoryCustom {

}
