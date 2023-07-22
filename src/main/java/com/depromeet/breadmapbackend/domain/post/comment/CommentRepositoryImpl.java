package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;

import lombok.RequiredArgsConstructor;

/**
 * CommentRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

	private final CommentJpaRepository commentJpaRepository;
	private final CommentQueryRepository commentQueryRepository;

	@Override
	public Comment save(final Comment comment) {
		return commentJpaRepository.save(comment);
	}

	@Override
	public Page<CommentInfo> findComment(final Long userId, final int page) {
		return null;//commentQueryRepository.findComment(userId, page);
	}

	@Override
	public Optional<Comment> findByIdAndUserId(final Long commentId, final Long userId) {
		return commentJpaRepository.findByIdAndUserId(commentId, userId);
	}
}
