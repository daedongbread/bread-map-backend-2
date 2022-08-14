package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    @Query(value = "select r from ReviewReport r", countQuery = "select count(*) from ReviewReport")
    Page<ReviewReport> findAll(Pageable pageable);
    void deleteByReview(Review review);
}
