package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLike;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLikeRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * CommentServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	@Override
	public Comment register(final Command command, final Long userId) {

		final Comment comment = command.toEntity(
			userRepository.findById(userId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND))
		);
		return commentRepository.save(comment);
	}

	@Override
	public Page<CommentInfo.Response> findComment(final Long postId, final Long userId, final int page) {
		return commentRepository.findComment(postId, userId, page)
			.map(info -> {
				final CommentResponseStatus status = getStatus(info.isBlocked(), info.status());

				return new CommentInfo.Response(
					info.id(),
					getContentToResponse(info, status),
					info.isFirstDepth(),
					info.parentId(),
					info.targetCommentUserNickname(),
					info.userId(),
					info.nickname(),
					info.nickname(),
					info.likeCount(),
					info.createdDate(),
					status
				);
			});
	}

	private String getContentToResponse(final CommentInfo info, final CommentResponseStatus status) {
		if (status != CommentResponseStatus.ACTIVE) {
			return status.replaceContent(info.content());
		}
		return info.isFirstDepth() ?
			info.content() :
			String.format("@%s %s", info.targetCommentUserNickname(), info.content());
	}

	private CommentResponseStatus getStatus(final boolean blocked, final CommentStatus status) {
		return blocked ? CommentResponseStatus.BLOCKED_BY_USER : status.getResponseStatus();
	}

	@Override
	@Transactional
	public void updateComment(final UpdateCommand command, final Long userId) {
		commentRepository.findByIdAndUserId(command.commentId(), userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.update(command.content());
	}

	@Transactional
	@Override
	public void deleteComment(final Long commentId, final Long userId) {
		commentRepository.findByIdAndUserId(commentId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.delete();
	}

	@Transactional
	@Override
	public int toggleLike(final Long commentId, final Long userId) {
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));

		final Optional<CommentLike> commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
		if (commentLike.isEmpty()) {
			commentLikeRepository.save(new CommentLike(comment, userId));
			return 1;
		} else {
			commentLikeRepository.delete(commentLike.get());
			return 0;
		}
	}
}
