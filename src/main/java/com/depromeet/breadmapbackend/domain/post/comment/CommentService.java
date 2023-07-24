package com.depromeet.breadmapbackend.domain.post.comment;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;

/**
 * CommentService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
public interface CommentService {
	Comment register(Command command, Long userId);

	Page<CommentInfo> findComment(
		Long postId,
		Long userId,
		int page
	);

	void updateComment(UpdateCommand command, Long userId);

	void deleteComment(Long commentId, Long userId);

	int toggleLike(Long commentId, Long id);
}
