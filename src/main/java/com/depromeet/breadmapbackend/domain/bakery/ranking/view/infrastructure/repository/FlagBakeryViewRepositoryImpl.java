package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.FlagBakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;

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
public class FlagBakeryViewRepositoryImpl implements FlagBakeryViewRepository {

	private final FlagBakeryRepository flagBakeryRepository;

	@Override
	public List<Long> findByUserIdAndBakeryIdIn(final Long userId, final List<Long> bakeryIds) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(userId, bakeryIds)
			.stream()
			.map(fb -> fb.getBakery().getId())
			.toList();
	}
}
