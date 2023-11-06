package com.depromeet.breadmapbackend.domain.admin.carousel.controller.dto;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.UpdateCarouselOrderCommand;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.EventCarouselResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.CarouselInfo;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */
public class Mapper {

	public static UpdateCarouselOrderCommand of(
		final UpdateCarouselOrderRequest request
	) {
		return new UpdateCarouselOrderCommand(
			request.id(),
			request.order()
		);
	}

	public static EventCarouselResponse of(final CarouselInfo info) {
		return new EventCarouselResponse(
			info.managerId(),
			info.order(),
			info.bannerImage()
		);
	}
}
