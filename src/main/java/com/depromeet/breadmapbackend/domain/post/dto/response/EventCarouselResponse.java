package com.depromeet.breadmapbackend.domain.post.dto.response;

/**
 * EventCarouselResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record EventCarouselResponse(
	int order,
	String title,
	String images
) {
}
