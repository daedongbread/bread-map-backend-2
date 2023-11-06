package com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command;

/**
 * UpdateEventOrderCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record UpdateCarouselOrderCommand(
	Long id,
	int order

) {
}
