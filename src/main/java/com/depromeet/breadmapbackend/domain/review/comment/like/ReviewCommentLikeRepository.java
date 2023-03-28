package com.depromeet.breadmapbackend.domain.review.comment.like;

import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewCommentLikeRepository extends JpaRepository<ReviewCommentLike, Long> {
    Optional<ReviewCommentLike> findByUserAndReviewComment(User user, ReviewComment reviewComment);
    void deleteByUser(User user);
}
