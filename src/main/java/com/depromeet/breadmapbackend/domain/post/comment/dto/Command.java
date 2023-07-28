package com.depromeet.breadmapbackend.domain.post.comment.dto;

import com.depromeet.breadmapbackend.domain.post.comment.Comment;
import com.depromeet.breadmapbackend.domain.user.User;

/**
 * CommentCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record Command(
	Long postId,
	String content,
	boolean isFirstDepth,
	Long parentId,
	Long targetCommentUserId
) {

	public Comment toEntity(final User user) {
		return new Comment(
			user,
			postId,
			content,
			isFirstDepth,
			isFirstDepth ? 0 : parentId,
			isFirstDepth ? 0 : targetCommentUserId
		);
	}
}
