package com.depromeet.breadmapbackend.domain.post.comment.dto;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.post.comment.CommentResponseStatus;
import com.depromeet.breadmapbackend.domain.post.comment.CommentStatus;

/**
 * CommentInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record CommentInfo(
	Long id,
	String content,
	boolean isFirstDepth,
	Long parentId,
	String targetCommentUserNickname,
	Long userId,
	String nickname,
	String profileImage,
	Long likeCount,
	LocalDate createdDate,
	CommentStatus status,
	boolean isBlocked
) {

	public record Response(
		Long id,
		String content,
		boolean isFirstDepth,
		Long parentId,
		String targetCommentUserNickname,
		Long userId,
		String nickname,
		String profileImage,
		Long likeCount,
		LocalDate createdDate,
		CommentResponseStatus responseStatus
	) {
	}
}
