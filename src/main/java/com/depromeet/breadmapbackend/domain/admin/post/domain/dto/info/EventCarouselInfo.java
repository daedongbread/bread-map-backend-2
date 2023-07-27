package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;

/**
 * EventCarouselInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record EventCarouselInfo(
	Long managerId,
	String title,
	int order,
	String bannerImage
) {
	public static EventCarouselInfo of(final PostManagerMapper pmm) {
		return new EventCarouselInfo(
			pmm.getId(),
			pmm.getPost().getTitle(),
			pmm.getCarouselOrder(),
			pmm.getBannerImage()
		);
	}
}
