package com.depromeet.breadmapbackend.domain.post.comment.dto.request;

import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

/**
 * CommentRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentCreateRequest(

	@NotNull Long postId,
	@NotNull @Size(min = 1, max = 200) String content,
	@NotNull boolean isFirstDepth,
	Optional<Long> parentId,
	Optional<Long> targetCommentUserId,
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	PostTopic postTopic
) {
}
