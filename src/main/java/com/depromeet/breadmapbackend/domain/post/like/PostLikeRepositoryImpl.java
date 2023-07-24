package com.depromeet.breadmapbackend.domain.post.like;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * PostLikeRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
@RequiredArgsConstructor
@Repository
public class PostLikeRepositoryImpl implements PostLikeRepository {

	private final PostLikeJpaRepository repository;

	@Override
	public Optional<PostLike> findByPostIdAndUserId(final Long postId, final Long userId) {
		return repository.findByPostIdAndUserId(postId, userId);
	}

	@Override
	public PostLike save(final PostLike postLike) {
		return repository.save(postLike);
	}

	@Override
	public void delete(final PostLike postLike) {
		repository.delete(postLike);
	}
}
