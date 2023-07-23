package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;

/**
 * CommentRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
public interface CommentRepository {
	Comment save(Comment comment);

	Page<CommentInfo> findComment(
		Long postId,
		Long userId,
		int page
	);

	Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

}
