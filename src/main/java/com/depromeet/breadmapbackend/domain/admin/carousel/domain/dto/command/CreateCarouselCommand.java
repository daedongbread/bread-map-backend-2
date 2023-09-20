package com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselType;

/**
 * CreateCarouselCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */
public record CreateCarouselCommand(
	CarouselType type,
	Long targetId,
	String bannerImage,
	boolean carouseled
) {
	public CarouselManager toCarousel(final int order) {
		return new CarouselManager(
			targetId,
			bannerImage,
			order,
			type,
			carouseled
		);
	}
}
