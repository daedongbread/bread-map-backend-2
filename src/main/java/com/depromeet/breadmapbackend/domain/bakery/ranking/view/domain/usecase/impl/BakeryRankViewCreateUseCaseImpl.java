package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewCreateEvent;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.ScoredBakeryExternalRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewCreateUseCase;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankViewCreateUseCaseImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryRankViewCreateUseCaseImpl implements BakeryRankViewCreateUseCase {

	private final ScoredBakeryExternalRepository scoredBakeryExternalRepository;
	private final BakeryRankViewCreateEvent bakeryRankViewCreateEvent;
	private final BakeryRankViewRepository bakeryRankViewRepository;

	@Override
	public void command() {
		final LocalDate now = LocalDate.now();
		final List<ScoredBakery> scoredBakeryList =
			scoredBakeryExternalRepository.findScoredBakeryByCalculatedDate(now);
		if (scoredBakeryList.isEmpty()) {
			bakeryRankViewCreateEvent.publish();
			throw new DaedongException(DaedongStatus.CALCULATING_BAKERY_RANKING);
		}

		final List<BakeryRankView> bakeryRankViewList = scoredBakeryList.stream()
			.map(sb -> BakeryRankView.builder()
				.rank(sb.rank())
				.bakeryId(sb.bakery().getId())
				.calculatedDate(sb.calculatedDate())
				.name(sb.bakery().getName())
				.image(sb.bakery().getImage())
				.rating(sb.rating())
				.flagCount(sb.flagCount())
				.shortAddress(sb.bakery().getShortAddress())
				.build()
			).toList();

		bakeryRankViewRepository.saveAll(bakeryRankViewList);
	}
}
