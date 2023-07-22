package com.depromeet.breadmapbackend.domain.post.comment;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;
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

	private static final String DELETED_COMMENT_CONTENT = "삭제된 댓글입니다.";
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;

	@Transactional
	@Override
	public void register(final Command command, final Long userId) {

		final Comment comment = command.toEntity(
			userRepository.findById(userId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND))
		);
		commentRepository.save(comment);
	}

	@Override
	public Page<CommentInfo> findComment(final int page, final Long userId) {
		return commentRepository.findComment(userId, page);
	}

	@Override
	@Transactional
	public void updateComment(final UpdateCommand command, final Long userId) {
		commentRepository.findByIdAndUserId(command.commentId(), userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.update(command.content());
	}

	@Override
	public void deleteComment(final Long commentId, final Long userId) {
		commentRepository.findByIdAndUserId(commentId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.update(DELETED_COMMENT_CONTENT);
	}
}
