package com.depromeet.breadmapbackend.domain.post.comment.dto.request;

/**
 * CommentUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentUpdateRequest(
	Long commentId,
	String content
) {
}
