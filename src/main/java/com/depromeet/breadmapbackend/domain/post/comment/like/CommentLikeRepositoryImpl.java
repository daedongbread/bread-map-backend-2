package com.depromeet.breadmapbackend.domain.post.comment.like;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * CommentLikeRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {

	private static final String TABLE = "comment_like";
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final CommentLikeJpaRepository commentLikeJpaRepository;

	@Override
	public Optional<CommentLike> findByCommentIdAndUserId(final Long commentId, final Long userId) {
		return commentLikeJpaRepository.findByCommentIdAndUserId(commentId, userId);
	}

	@Override
	public CommentLike save(final CommentLike commentLike) {
		return commentLikeJpaRepository.save(commentLike);
	}

	@Override
	public void delete(final CommentLike commentLike) {
		commentLikeJpaRepository.delete(commentLike);
	}

	@Override
	public List<CommentLike> findByPostId(final Long postId) {
		return null;
	}

	@Override
	public void deleteAllByCommentIdList(final List<Long> commentIdList) {
		String sql = String.format(
			"""
					delete
					from `%s` 
					where comment_id = :commentId 
				""", TABLE);

		final SqlParameterSource[] params = commentIdList.stream()
			.map(commentId -> new MapSqlParameterSource()
				.addValue("commentId", commentId)
			)
			.toArray(SqlParameterSource[]::new);
		jdbcTemplate.batchUpdate(sql, params);
	}
}
