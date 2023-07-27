package com.depromeet.breadmapbackend.domain.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public interface PostJpaRepository extends JpaRepository<Post, Long> {

	Optional<Post> findByIdAndUserIdAndPostTopic(Long id, Long userId, PostTopic postTopic);

	Optional<Post> findByIdAndPostTopic(Long postId, PostTopic postTopic);

}
