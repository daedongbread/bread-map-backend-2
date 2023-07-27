package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentQuery;

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
	public Page<CommentInfo> findComment(
		final Long postId,
		final Long userId,
		final int page
	) {
		return commentQueryRepository.findComment(postId, userId, page)
			.map(CommentQuery::toInfo);
	}

	@Override
	public Optional<Comment> findByIdAndUserId(final Long commentId, final Long userId) {
		return commentJpaRepository.findByIdAndUserId(commentId, userId);
	}

	@Override
	public void deleteAllByIdInBatch(final List<Long> commentIdList) {
		commentJpaRepository.deleteAllByIdInBatch(commentIdList);

	}

	@Override
	public List<Long> findCommentIdListByPostId(final Long postId) {
		return commentJpaRepository.findCommentIdListByPostId(postId);
	}

	@Override
	public Optional<Comment> findById(final Long id) {
		return commentJpaRepository.findById(id);
	}
}
