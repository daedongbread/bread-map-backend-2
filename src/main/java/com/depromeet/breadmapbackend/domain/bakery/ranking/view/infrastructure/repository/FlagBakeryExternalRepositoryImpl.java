package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.FlagBakeryExternalRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;

import lombok.RequiredArgsConstructor;

/**
 * FlagBakeryExternalRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Repository
@RequiredArgsConstructor
public class FlagBakeryExternalRepositoryImpl implements FlagBakeryExternalRepository {

	private final FlagBakeryRepository flagBakeryRepository;

	@Override
	public Long countFlagBakeryByBakeryId(final Long bakeryId) {
		final List<FlagBakeryRepository.FlagBakeryCount> count =
			flagBakeryRepository.countFlagBakeryByBakeryIdIn(List.of(bakeryId));
		return count.isEmpty() ? 0L : count.get(0).getCount();
	}
}
