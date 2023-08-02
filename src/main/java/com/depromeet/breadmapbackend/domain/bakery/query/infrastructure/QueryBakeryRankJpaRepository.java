package com.depromeet.breadmapbackend.domain.bakery.query.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * QueryBakeryRankJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface QueryBakeryRankJpaRepository extends JpaRepository<QueryBakeryRankJpaEntity, Long> {
}
