package com.depromeet.breadmapbackend.domain.bakery.query.domain.usecase.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryFlagCount;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.BakeryRanksCachingPublisher;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.QueryBakeryRankRepository;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.usecase.QueryBakeryRankUseCase;

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
public class QueryBakeryRankService implements QueryBakeryRankUseCase {

	private final QueryBakeryRankRepository queryBakeryRankRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final BakeryRanksCachingPublisher bakeryRanksCachingPublisher;

	@Override
	public List<Query> query(final Long userId, final int size) {
		final LocalDate currentDate = LocalDate.now();
		final Pageable page = Pageable.ofSize(size);

		final List<QueryBakeryRank> bakeryRanksView = queryBakeryRankRepository.findByCalculatedDate(currentDate, page);
		if (!bakeryRanksView.isEmpty()) {
			return toQuery(userId, bakeryRanksView);
		}

		bakeryRanksCachingPublisher.publish();
		return toQuery(userId, queryBakeryRankRepository.findByCalculatedDateFromDb(currentDate, page));
	}

	private List<Long> getBakeryIdsFrom(final List<QueryBakeryRank> bakeryRanks) {
		return bakeryRanks.stream()
			.map(QueryBakeryRank::bakeryId)
			.toList();
	}

	private List<Query> toQuery(
		final Long userId,
		final List<QueryBakeryRank> bakeryRanks
	) {
		final List<Long> bakeryIds = getBakeryIdsFrom(bakeryRanks);
		return bakeryRanks.stream()
			.map(br -> new Query(
				br.bakeryId(),
				br.name(),
				br.image(),
				getBakeryFlaggedCount(bakeryIds, br),
				br.rating(),
				br.shortAddress(),
				isUserFlaggedBakery(userId, bakeryIds, br),
				br.calculatedDate()
			)).toList();
	}

	private boolean isUserFlaggedBakery(final Long userId, final List<Long> bakeryIds, final QueryBakeryRank br) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(
			userId,
			bakeryIds
		).stream().anyMatch(fb -> fb.equals(br.bakeryId()));
	}

	private Long getBakeryFlaggedCount(final List<Long> bakeryIds, final QueryBakeryRank br) {
		return flagBakeryRepository.countFlagBakeryByBakeryIdIn(bakeryIds).stream()
			.filter(fc -> fc.bakeryId().equals(br.bakeryId()))
			.findFirst()
			.map(QueryBakeryFlagCount::flagCount).orElse(0L);
	}
}
