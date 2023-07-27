package com.depromeet.breadmapbackend.domain.post.dto;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;

/**
 * EventCarouselInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record EventCarouselInfo(
	int order,
	String title,
	String image
) {

	public static EventCarouselInfo of(final PostManagerMapper pmm) {
		return new EventCarouselInfo(pmm.getCarouselOrder(), pmm.getPost().getTitle(), pmm.getBannerImage());
	}
}
