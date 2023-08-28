package com.depromeet.breadmapbackend.domain.post.comment.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommentLikeJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {
	Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
}
