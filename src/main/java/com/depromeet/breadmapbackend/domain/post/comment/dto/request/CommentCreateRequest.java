package com.depromeet.breadmapbackend.domain.post.comment.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CommentRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentCreateRequest(

	@NotNull Long postId,
	@NotNull @Size(min = 10, max = 200) String content,
	@NotNull boolean isFirstDepth,
	@NotNull Long parentId,
	@NotNull Long targetCommentUserId
) {
}
