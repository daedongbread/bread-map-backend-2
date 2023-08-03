package com.depromeet.breadmapbackend.domain.bakery.ranking.query.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * QueryBakeryRankJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface QueryBakeryRankJpaRepository extends JpaRepository<QueryBakeryRankJpaEntity, Long> {

	List<QueryBakeryRankJpaEntity> findByCalculatedDate(final LocalDate calculatedDate, Pageable pageable);
}
