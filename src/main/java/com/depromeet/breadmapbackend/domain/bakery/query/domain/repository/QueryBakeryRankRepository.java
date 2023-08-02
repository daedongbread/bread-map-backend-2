package com.depromeet.breadmapbackend.domain.bakery.query.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;

/**
 * QueryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface QueryBakeryRankRepository {

	List<QueryBakeryRank> findByCalculatedDate(LocalDate calculatedDate, Pageable pageable);

	List<QueryBakeryRank> findByCalculatedDateFromDb(LocalDate calculatedDate, Pageable pageable);
}
