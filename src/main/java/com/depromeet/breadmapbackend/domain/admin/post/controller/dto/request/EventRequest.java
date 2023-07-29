package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CreateEventRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record EventRequest(
	@NotNull boolean isPosted,
	@NotNull boolean isFixed,
	@NotNull boolean isCarousel,
	@NotNull @Size(min = 10, max = 40) String title,
	@NotNull @Size(min = 10, max = 400) String content,
	String bannerImage,
	@Size(max = 10) List<String> images
) {
}
