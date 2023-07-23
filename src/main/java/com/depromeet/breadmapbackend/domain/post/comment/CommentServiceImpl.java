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
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

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

	private static final String BLOCKED_USER_COMMENT = "차단된 유저의 댓글 입니다.";
	private static final String BLOCKED_USER_NICKNAME = "차단된 유저 입니다.";
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final CustomAWSS3Properties customAWSS3Properties;

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
	public Page<CommentInfo> findComment(final Long postId, final Long userId, final int page) {
		return commentRepository.findComment(postId, userId, page)
			.map(info -> new CommentInfo(
				info.id(),
				getContent(info.isBlocked(), info.status(), info.content()),
				info.isFirstDepth(),
				info.parentId(),
				info.userId(),
				info.isBlocked() ? BLOCKED_USER_NICKNAME : info.nickname(),
				info.isBlocked() ? getDefaultImage() : info.nickname(),
				info.likeCount(),
				info.createdDate(),
				info.status(),
				info.isBlocked()
			));
	}

	private String getDefaultImage() {
		return customAWSS3Properties.getCloudFront() + "/" +
			customAWSS3Properties.getDefaultImage().getUser() + ".png";
	}

	private String getContent(final boolean blocked, final CommentStatus status, final String content) {
		return blocked ? BLOCKED_USER_COMMENT : status.replaceContent(content);
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
			.delete();
	}
}
