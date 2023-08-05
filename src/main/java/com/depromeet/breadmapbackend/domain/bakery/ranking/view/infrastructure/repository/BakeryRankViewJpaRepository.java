package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * QueryBakeryRankJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface BakeryRankViewJpaRepository extends JpaRepository<BakeryRankViewJpaEntity, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	Optional<BakeryRankViewJpaEntity> findByBakeryIdAndCalculatedDate(
		final Long bakeryId,
		final LocalDate calculatedDate
	);

	// findByBakeryIdInOrderByCalculatedDateDesc
	List<BakeryRankViewJpaEntity> findByBakeryIdInAndCalculatedDate(
		final List<Long> bakeryIds,
		final LocalDate calculatedDate,
		Pageable pageable
	);

	List<BakeryRankViewJpaEntity> findByCalculatedDateOrderByBakeryRankAsc(final LocalDate calculatedDate,
		Pageable pageable);

	@Modifying
	@Query("delete from BakeryRankViewJpaEntity brv where brv.calculatedDate = :calculatedDate")
	void deleteAllByCalculatedDate(@Param("calculatedDate") LocalDate calculatedDate);
 
}
