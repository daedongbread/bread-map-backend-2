package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure.ScoredBakeryJpaEntity;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure.ScoredBakeryJpaRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.ScoredBakeryExternalRepository;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Repository
@RequiredArgsConstructor
public class ScoredBakeryExternalExternalRepositoryImpl implements ScoredBakeryExternalRepository {

	private final ScoredBakeryJpaRepository scoredBakeryJpaRepository;

	@Override
	public List<ScoredBakery> findScoredBakeryByCalculatedDate(final LocalDate calculatedDate) {
		return scoredBakeryJpaRepository.findScoredBakeryByCalculatedDate(calculatedDate)
			.stream()
			.map(ScoredBakeryJpaEntity::toDomain)
			.toList();
	}

}
