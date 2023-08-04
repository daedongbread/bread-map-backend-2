package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewCreateUseCase;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewRankChangeUseCase;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * BakeryRankViewRankChangeUseCaseImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */

@Component
@RequiredArgsConstructor
public class BakeryRankViewRankChangeUseCaseImpl implements BakeryRankViewRankChangeUseCase {

	private final BakeryRankViewRepository bakeryRankViewRepository;
	private final BakeryRankViewCreateUseCase bakeryRankViewCreateUseCase;

	@Override
	public void command(final List<Command> command) {
		final LocalDate now = LocalDate.now();
		final List<Long> bakeryIdList = command.stream()
			.map(Command::bakeryId)
			.toList();
		final Pageable page = Pageable.ofSize(bakeryIdList.size());

		final List<BakeryRankView> bakeryRankViewList =
			bakeryRankViewRepository.findByBakeryIdInAndCalculatedDate(bakeryIdList, now, page);

		if (bakeryRankViewList.size() != bakeryIdList.size()) {
			bakeryRankViewCreateUseCase.command();
			throw new DaedongException(DaedongStatus.BAKERY_RANKING_NOT_FOUND);
		}
		final List<BakeryRankView> updatedBakeryRankView = updateBakeryRankViewRank(command, bakeryRankViewList);
		bakeryRankViewRepository.saveAll(updatedBakeryRankView);
	}

	private static List<BakeryRankView> updateBakeryRankViewRank(final List<Command> command,
		final List<BakeryRankView> bakeryRankViewList) {
		return bakeryRankViewList.stream()
			.map(br -> {
				final Command commandBy = command.stream()
					.filter(c -> c.bakeryId().equals(br.bakeryId()))
					.findFirst()
					.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_RANKING_NOT_FOUND));
				return br.updateRank(commandBy.rank());
			}).toList();
	}
}
