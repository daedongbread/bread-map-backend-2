package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.ReviewExternalRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewRatingCountChangeUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankViewLikeCountChangeUseCaseImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankViewRatingCountChangeUseCaseImpl implements BakeryRankViewRatingCountChangeUseCase {

	private final BakeryRankViewRepository bakeryRankViewRepository;
	private final ReviewExternalRepository reviewExternalRepository;

	@Override
	public void command(final Long bakeryId) {
		final LocalDate now = LocalDate.now();
		final Optional<BakeryRankView> bakeryRankView =
			bakeryRankViewRepository.findByBakeryIdAndCalculatedDate(bakeryId, now);

		if (bakeryRankView.isPresent()) {
			final double rating = reviewExternalRepository.findAvgRatingByBakeryId(bakeryId);
			bakeryRankViewRepository.save(bakeryRankView.get().updateRating(rating));
		}
	}
}
