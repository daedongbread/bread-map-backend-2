package com.depromeet.breadmapbackend.domain.admin.carousel.domain.service;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.CreateCarouselCommand;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.UpdateCarouselOrderCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.CarouselInfo;

/**
 * CarouselService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */
public interface CarouselManagerService {

	void saveCarousel(final CreateCarouselCommand command);

	void updateCarouselOrder(final List<UpdateCarouselOrderCommand> commands);

	List<CarouselInfo> getCarousels();

	void toggleCarousel(final Long carouselId, final boolean isCarousel);
}
