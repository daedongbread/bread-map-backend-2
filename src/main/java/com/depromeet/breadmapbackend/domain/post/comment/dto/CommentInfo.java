package com.depromeet.breadmapbackend.domain.post.comment.dto;

import java.time.LocalDate;

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
	Long userId,
	String nickname,
	String profileImage,
	Long likeCount,
	LocalDate createdDate,
	CommentStatus status,
	boolean isBlocked
) {
	public CommentInfo toDisplayInfo() {
		return new CommentInfo(
			id,
			content,
			isFirstDepth,
			parentId,
			userId,
			nickname,
			profileImage,
			likeCount,
			createdDate,
			status,
			isBlocked
		);
	}

}
