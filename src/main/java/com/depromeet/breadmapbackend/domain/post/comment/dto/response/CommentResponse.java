package com.depromeet.breadmapbackend.domain.post.comment.dto.response;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.post.comment.CommentResponseStatus;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;

/**
 * CommentResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentResponse(
	Long id,
	String content,
	boolean isFirstDepth,
	Long parentId,
	String targetCommentUserNickname,
	Long userId,
	String nickname,
	String profileImage,
	Long likeCount,
	boolean isUserLiked,
	LocalDate createdDate,
	CommentResponseStatus status
) {
	public CommentResponse(final CommentInfo.Response response) {
		this(
			response.id(),
			response.content(),
			response.isFirstDepth(),
			response.parentId(),
			response.targetCommentUserNickname(),
			response.userId(),
			response.nickname(),
			response.profileImage(),
			response.likeCount(),
			response.isUserLiked(),
			response.createdDate(),
			response.responseStatus()
		);
	}
}
