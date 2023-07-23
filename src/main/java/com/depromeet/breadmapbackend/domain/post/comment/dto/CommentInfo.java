package com.depromeet.breadmapbackend.domain.post.comment.dto;

import java.time.LocalDate;

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
	LocalDate createdDate
) {
}
