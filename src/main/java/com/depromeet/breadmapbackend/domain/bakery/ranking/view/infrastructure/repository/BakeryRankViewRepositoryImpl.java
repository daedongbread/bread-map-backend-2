package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.BakeryRankViewRepository;

import lombok.RequiredArgsConstructor;

/**
 * QueryBakeryRankRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
@Repository
@RequiredArgsConstructor
public class BakeryRankViewRepositoryImpl implements BakeryRankViewRepository {

	private final BakeryRankViewJpaRepository bakeryRankViewJpaRepository;

	@Override
	public List<BakeryRankView> findBakeryRankViewBy(final LocalDate calculatedDate, final Pageable pageable) {
		return bakeryRankViewJpaRepository.findByCalculatedDateOrderByBakeryRankAsc(calculatedDate, pageable)
			.stream()
			.map(BakeryRankViewJpaEntity::toDomain)
			.toList();
	}

	@Override
	public Optional<BakeryRankView> findByBakeryIdAndCalculatedDate(final Long bakeryId,
		final LocalDate calculatedDate) {
		return bakeryRankViewJpaRepository.findByBakeryIdAndCalculatedDate(bakeryId, calculatedDate)
			.map(BakeryRankViewJpaEntity::toDomain);

	}

	@Override
	public BakeryRankView save(final BakeryRankView updatedBakeryRankView) {
		return bakeryRankViewJpaRepository.save(BakeryRankViewJpaEntity.fromDomain(updatedBakeryRankView))
			.toDomain();
	}

	@Override
	public List<BakeryRankView> findByBakeryIdInAndCalculatedDate(
		final List<Long> bakeryIdList,
		final LocalDate now,
		final Pageable page
	) {
		return bakeryRankViewJpaRepository.findByBakeryIdInAndCalculatedDate(bakeryIdList, now, page)
			.stream()
			.map(BakeryRankViewJpaEntity::toDomain)
			.toList();
	}

	@Override
	public void saveAll(final List<BakeryRankView> updatedBakeryRankView) {
		bakeryRankViewJpaRepository.saveAll(updatedBakeryRankView.stream()
			.map(BakeryRankViewJpaEntity::fromDomain)
			.toList());
	}

	@Override
	public void deleteAllByCalculatedDate(final LocalDate calculatedDate) {
		bakeryRankViewJpaRepository.deleteAllByCalculatedDate(calculatedDate);
	}
}
