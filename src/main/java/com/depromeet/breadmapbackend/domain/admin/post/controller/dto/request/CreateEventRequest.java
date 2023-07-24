package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request;

import java.util.List;

/**
 * CreateEventRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record CreateEventRequest(
	boolean isPosted,
	boolean isFixed,
	boolean isCarousel,
	String title,
	String content,
	String bannerImage,
	List<String> images
) {
}
