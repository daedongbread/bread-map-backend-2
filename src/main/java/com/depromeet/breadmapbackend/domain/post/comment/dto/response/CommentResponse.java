package com.depromeet.breadmapbackend.domain.post.comment.dto.response;

import java.time.LocalDate;

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
	Long userId,
	String nickname,
	String profileImage,
	Long likeCount,
	LocalDate createdDate
) {
	public CommentResponse(final CommentInfo info) {
		this(
			info.id(),
			info.content(),
			info.isFirstDepth(),
			info.parentId(),
			info.userId(),
			info.nickname(),
			info.profileImage(),
			info.likeCount(),
			info.createdDate()
		);
	}
}
