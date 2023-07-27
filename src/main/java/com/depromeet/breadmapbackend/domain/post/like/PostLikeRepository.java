package com.depromeet.breadmapbackend.domain.post.like;

import java.util.Optional;

/**
 * PostLikeRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface PostLikeRepository {
	Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

	PostLike save(PostLike postLike);

	void delete(PostLike postLike);

	void deleteByPostId(Long postId);
}
