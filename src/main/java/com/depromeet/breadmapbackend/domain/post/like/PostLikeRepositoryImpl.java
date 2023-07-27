package com.depromeet.breadmapbackend.domain.post.like;

import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
	private static final String TABLE = "post_like";
	private final PostLikeJpaRepository repository;
	private final NamedParameterJdbcTemplate jdbcTemplate;

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

	@Override
	public void deleteByPostId(final Long postId) {
		repository.deleteByPostId(postId);
	}
}
