package com.depromeet.breadmapbackend.domain.review.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.depromeet.breadmapbackend.domain.review.Review;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
	@Query(value = "select r from ReviewReport r", countQuery = "select count(r) from ReviewReport r")
	Page<ReviewReport> findPageAll(Pageable pageable);

	void deleteByReview(Review review);

	Long countByIsBlock(Boolean isBlock);
}
