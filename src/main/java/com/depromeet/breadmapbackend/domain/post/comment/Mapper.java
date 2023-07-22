package com.depromeet.breadmapbackend.domain.post.comment;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;
import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentCreateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentUpdateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.response.CommentResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class Mapper {

	public static Command of(CommentCreateRequest request) {
		return new Command(
			request.postId(),
			request.content(),
			request.isFirstDepth(),
			request.parentId()
		);
	}

	public static UpdateCommand of(final CommentUpdateRequest request) {
		return new UpdateCommand(
			request.commentId(),
			request.content()
		);
	}

	public static PageResponseDto<CommentResponse> of(final Page<CommentInfo> comment) {
		return PageResponseDto.of(comment, CommentResponse::new);

	}
}
