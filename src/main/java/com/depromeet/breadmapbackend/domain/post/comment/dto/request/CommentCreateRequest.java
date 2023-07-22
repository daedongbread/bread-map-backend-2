package com.depromeet.breadmapbackend.domain.post.comment.dto.request;

/**
 * CommentRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentCreateRequest(
	Long postId,
	String content,
	boolean isFirstDepth,
	Long parentId
) {
}
