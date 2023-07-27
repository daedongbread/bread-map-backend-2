package com.depromeet.breadmapbackend.domain.post.comment.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CommentUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentUpdateRequest(
	@NotNull Long commentId,
	@Size(min = 10, max = 200) String content
) {
}
