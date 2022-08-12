package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.ReviewComment;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByReviewIdAndParentIsNull(Long reviewId);
    Optional<ReviewComment> findByIdAndUser(Long id, User user);
    void deleteByUser(User user);
}
