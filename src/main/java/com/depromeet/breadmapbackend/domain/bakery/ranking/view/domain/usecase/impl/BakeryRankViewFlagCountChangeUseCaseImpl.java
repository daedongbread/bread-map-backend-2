package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.FlagBakeryExternalRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewFlagCountChangeUseCase;

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
	private final FlagBakeryExternalRepository flagBakeryExternalRepository;

	@Override
	public void command(final Long bakeryId) {
		final LocalDate now = LocalDate.now();
		final Optional<BakeryRankView> bakeryRankView =
			bakeryRankViewRepository.findByBakeryIdAndCalculatedDate(bakeryId, now);

		if (bakeryRankView.isPresent()) {
			final Long flagCount = flagBakeryExternalRepository.countFlagBakeryByBakeryId(bakeryId);
			final BakeryRankView updatedBakeryRankView = bakeryRankView.get().updateFlagCount(flagCount);
			bakeryRankViewRepository.save(updatedBakeryRankView);
		}
	}
}
