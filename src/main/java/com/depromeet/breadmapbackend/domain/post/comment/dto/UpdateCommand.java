package com.depromeet.breadmapbackend.domain.post.comment.dto;

/**
 * UpdateCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public record UpdateCommand(
	Long commentId,
	String content
) {
}
