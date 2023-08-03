package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.BakeryScoreBase;

/**
 * BakeryBaseScoreRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
public interface BakeryBaseScoreRepository {
	List<BakeryScoreBase> getBakeriesScoreFactors(LocalDate calculateDate);
}
