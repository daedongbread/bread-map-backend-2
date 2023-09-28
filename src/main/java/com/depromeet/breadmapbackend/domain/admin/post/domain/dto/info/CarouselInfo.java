package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselType;

/**
 * EventCarouselInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record CarouselInfo(
	Long managerId,
	int order,
	String bannerImage,
	CarouselType type,
	Long targetId
) {
	public static CarouselInfo of(final CarouselManager carouselManager) {
		return new CarouselInfo(
			carouselManager.getId(),
			carouselManager.getCarouselOrder(),
			carouselManager.getBannerImage(),
			carouselManager.getCarouselType(),
			carouselManager.getTargetId()
		);
	}
}
