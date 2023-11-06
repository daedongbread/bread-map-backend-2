package com.depromeet.breadmapbackend.domain.admin.carousel.domain.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.CreateCarouselCommand;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.UpdateCarouselOrderCommand;
import com.depromeet.breadmapbackend.domain.admin.carousel.repository.CarouselRepository;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.CarouselInfo;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * CarouselServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CarouselManagerServiceImpl implements CarouselManagerService {
	private static final int MAX_EVENT_CAROUSEL_COUNT = 20;
	private final CarouselRepository carouselRepository;

	@Transactional
	@Override
	public void saveCarousel(final CreateCarouselCommand command) {
		final List<CarouselManager> carousels =
			carouselRepository.findByCarouseledIsOrderByCarouselOrderAsc(true);

		if (command.carouseled()) {
			carousels.forEach(carousel -> {
				final int order = carousels.indexOf(carousel);
				if (order >= MAX_EVENT_CAROUSEL_COUNT) { // TODO : 정책 확인 필요
					carousel.toggleCarousel(false, 0);
				}
				carousel.updateCarouselOrder(order + 2);
			});
		}

		carouselRepository.save(
			command.toCarousel(command.carouseled() ? 1 : 0)
		);

	}

	@Transactional
	@Override
	public void updateCarouselOrder(final List<UpdateCarouselOrderCommand> commands) {
		final List<CarouselManager> carousels =
			carouselRepository.findByCarouseledIsOrderByCarouselOrderAsc(true);

		carousels
			.forEach(carousel -> commands.stream()
				.filter(command -> command.id().equals(carousel.getId()))
				.findFirst()
				.ifPresent(command -> carousel.updateCarouselOrder(command.order())));
	}

	@Transactional
	@Override
	public void toggleCarousel(final Long carouselId, final boolean isCarousel) {

		carouselRepository.findById(carouselId)
			.ifPresent(carousel -> {
				if (carousel.isCarouseled() == isCarousel)
					return;

				if (carousel.isCarouseled())
					carousel.toggleCarousel(false, 0);

				else {
					final List<CarouselManager> carousels =
						carouselRepository.findByCarouseledIsOrderByCarouselOrderAsc(true);

					if (carousels.size() >= MAX_EVENT_CAROUSEL_COUNT) // TODO : 정책 확인 필요
						throw new DaedongException(DaedongStatus.CAROUSEL_POST_COUNT_EXCEEDED);

					carousel.toggleCarousel(
						true,
						carousels.stream()
							.max(Comparator.comparingInt(CarouselManager::getCarouselOrder))
							.map(CarouselManager::getCarouselOrder)
							.orElse(0) + 1
					);
				}
			});
	}

	@Override
	public List<CarouselInfo> getCarousels() {
		return carouselRepository.findByCarouseledIsOrderByCarouselOrderAsc(true)
			.stream()
			.map(CarouselInfo::of)
			.toList();
	}

}
