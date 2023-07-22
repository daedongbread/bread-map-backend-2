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
	void register(Command command, Long userId);

	Page<CommentInfo> findComment(int page, Long userId);

	void updateComment(UpdateCommand command, Long userId);

	void deleteComment(Long commentId, Long userId);
}
