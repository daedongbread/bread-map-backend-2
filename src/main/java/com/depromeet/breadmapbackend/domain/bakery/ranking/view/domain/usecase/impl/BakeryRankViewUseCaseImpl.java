package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.FlagBakeryViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewCreateUseCase;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewUseCase;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * QueryBakeryRankService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
@Service
@RequiredArgsConstructor
public class BakeryRankViewUseCaseImpl implements BakeryRankViewUseCase {

	private final BakeryRankViewRepository bakeryRankViewRepository;
	private final FlagBakeryViewRepository FlagBakeryViewRepository;
	private final BakeryRankViewCreateUseCase bakeryRankViewCreateUseCase;

	@Override
	public List<Query> query(final Long userId, final int size) {
		final LocalDate currentDate = LocalDate.now();
		final Pageable page = Pageable.ofSize(size);
		final List<BakeryRankView> bakeryRankViewBy = bakeryRankViewRepository.findBakeryRankViewBy(currentDate, page);
		if (bakeryRankViewBy.isEmpty()) {
			bakeryRankViewCreateUseCase.command();
			throw new DaedongException(DaedongStatus.BAKERY_RANKING_NOT_FOUND);
		}
		return toQuery(userId, bakeryRankViewBy);
	}

	private List<Long> getBakeryIdsFrom(final List<BakeryRankView> bakeryRanks) {
		return bakeryRanks.stream()
			.map(BakeryRankView::bakeryId)
			.toList();
	}

	private List<Query> toQuery(
		final Long userId,
		final List<BakeryRankView> bakeryRanks
	) {
		final List<Long> bakeryIds = getBakeryIdsFrom(bakeryRanks);
		final List<Long> userFlaggedBakeryIds = getUserFlaggedBakeries(userId, bakeryIds);
		return bakeryRanks.stream()
			.map(br -> new Query(
				br.bakeryId(),
				br.name(),
				br.image(),
				br.flagCount(),
				br.rating(),
				br.shortAddress(),
				isUserFlaggedBakery(userFlaggedBakeryIds, br),
				br.calculatedDate()
			)).toList();
	}

	private List<Long> getUserFlaggedBakeries(final Long userId, final List<Long> bakeryIds) {
		return FlagBakeryViewRepository.findByUserIdAndBakeryIdIn(userId, bakeryIds);
	}

	private boolean isUserFlaggedBakery(final List<Long> userFlaggedBakeryIds, final BakeryRankView br) {
		return userFlaggedBakeryIds
			.stream()
			.anyMatch(fb -> fb.equals(br.bakeryId()));
	}
}
