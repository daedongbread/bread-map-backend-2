package com.depromeet.breadmapbackend.domain.post.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostLikeJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {
	Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
}
