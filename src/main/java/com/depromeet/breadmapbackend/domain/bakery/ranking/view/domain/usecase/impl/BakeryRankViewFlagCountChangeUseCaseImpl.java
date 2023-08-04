package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewCreateUseCase;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewFlagCountChangeUseCase;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankViewFlagCountChangeUseCaseImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BakeryRankViewFlagCountChangeUseCaseImpl implements BakeryRankViewFlagCountChangeUseCase {

	private final BakeryRankViewRepository bakeryRankViewRepository;
	private final BakeryRankViewCreateUseCase bakeryRankViewCreateUseCase;

	@Override
	public void command(final Long bakeryId) {
		final LocalDate now = LocalDate.now();
		final BakeryRankView bakeryRankView =
			bakeryRankViewRepository.findByBakeryIdAndCalculatedDate(bakeryId, now)
				.orElseThrow(() -> {
					bakeryRankViewCreateUseCase.command();
					return new DaedongException(DaedongStatus.BAKERY_RANKING_NOT_FOUND);
				});

		final BakeryRankView updatedBakeryRankView = bakeryRankView.updateFlagCount(flagCount);
		bakeryRankViewRepository.save(updatedBakeryRankView);
	}
}
