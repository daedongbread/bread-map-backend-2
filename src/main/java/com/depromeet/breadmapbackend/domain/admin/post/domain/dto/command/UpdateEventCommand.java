package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command;

import java.util.List;

/**
 * UpdateEventCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record UpdateEventCommand(
	Long postId,
	boolean isPosted,
	boolean isFixed,
	boolean isCarousel,
	String title,
	String content,
	String bannerImage,
	List<String> images
) {
}
