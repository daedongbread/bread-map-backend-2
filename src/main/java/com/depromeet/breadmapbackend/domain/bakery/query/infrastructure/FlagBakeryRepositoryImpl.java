package com.depromeet.breadmapbackend.domain.bakery.query.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryFlagCount;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.FlagBakeryRepository;

import lombok.RequiredArgsConstructor;

/**
 * UserFlaggedBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */

@Repository
@RequiredArgsConstructor
public class FlagBakeryRepositoryImpl implements FlagBakeryRepository {

	private final com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository flagBakeryRepository;

	@Override
	public List<Long> findByUserIdAndBakeryIdIn(final Long userId, final List<Long> bakeryIds) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(userId, bakeryIds)
			.stream()
			.map(fb -> fb.getBakery().getId())
			.toList();
	}

	@Override
	public List<QueryBakeryFlagCount> countFlagBakeryByBakeryIdIn(final List<Long> bakeryIds) {
		return flagBakeryRepository.countFlagBakeryByBakeryIdIn(bakeryIds)
			.stream()
			.map(fc -> new QueryBakeryFlagCount(fc.getBakeryId(), fc.getCount()))
			.toList();
	}
}
