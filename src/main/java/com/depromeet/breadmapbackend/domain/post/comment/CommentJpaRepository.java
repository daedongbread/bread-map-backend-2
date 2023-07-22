package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommentJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByIdAndUserId(Long commentId, Long userId);
}
