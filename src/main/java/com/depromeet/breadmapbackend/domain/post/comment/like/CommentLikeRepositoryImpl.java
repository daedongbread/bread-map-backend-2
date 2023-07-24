package com.depromeet.breadmapbackend.domain.post.comment.like;

import java.util.Optional;

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
}
