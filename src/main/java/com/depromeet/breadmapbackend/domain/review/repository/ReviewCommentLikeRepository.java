package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.review.ReviewComment;
import com.depromeet.breadmapbackend.domain.review.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewCommentLikeRepository extends JpaRepository<ReviewCommentLike, Long> {
    Optional<ReviewCommentLike> findByUserAndReviewComment(User user, ReviewComment reviewComment);
    void deleteByUser(User user);
}
