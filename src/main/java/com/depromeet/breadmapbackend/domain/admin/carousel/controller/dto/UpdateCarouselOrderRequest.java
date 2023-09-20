package com.depromeet.breadmapbackend.domain.admin.carousel.controller.dto;

import javax.validation.constraints.NotNull;

/**
 * UpdateEventOrderRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record UpdateCarouselOrderRequest(
	@NotNull Long id,
	@NotNull int order
) {
}
