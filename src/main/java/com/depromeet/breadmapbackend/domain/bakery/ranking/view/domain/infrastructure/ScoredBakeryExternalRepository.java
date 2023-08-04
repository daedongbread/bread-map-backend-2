package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;

/**
 * ScoredBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface ScoredBakeryExternalRepository {
	List<ScoredBakery> findScoredBakeryByCalculatedDate(final LocalDate calculatedDate);

}
