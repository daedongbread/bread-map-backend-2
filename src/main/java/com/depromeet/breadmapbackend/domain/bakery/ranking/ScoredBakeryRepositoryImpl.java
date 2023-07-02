package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Repository
@RequiredArgsConstructor
public class ScoredBakeryRepositoryImpl implements ScoredBakeryRepository {

	private final ScoredBakeryJpaRepository scoredBakeryJpaRepository;

	@Override
	public int bulkInsert(final List<ScoredBakery> scoredBakeryList) {
		return 0;
	}

	@Override
	public List<ScoredBakery> findBakeriesRankTop(final int count) {
		return scoredBakeryJpaRepository.findBakeriesRankTop(Pageable.ofSize(count));
	}
}
