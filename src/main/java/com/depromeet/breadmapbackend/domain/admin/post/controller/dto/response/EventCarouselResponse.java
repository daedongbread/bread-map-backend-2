package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response;

/**
 * EventCarouselResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record EventCarouselResponse(
	Long managerId,
	String title,
	int order,
	String bannerImage

) {
}
