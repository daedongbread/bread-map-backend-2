package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.BakeryQueryRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.BakeryScoreBase;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryBaseScoreRepository;

import lombok.RequiredArgsConstructor;

/**
 * BakeryBaseScoreRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
@Repository
@RequiredArgsConstructor
public class BakeryBaseScoreRepositoryImpl implements BakeryBaseScoreRepository {

	private final BakeryQueryRepository bakeryQueryRepository;

	@Override
	public List<BakeryScoreBase> getBakeriesScoreFactors(final LocalDate calculateDate) {
		return bakeryQueryRepository.getBakeriesScoreFactors(calculateDate);
	}
}
