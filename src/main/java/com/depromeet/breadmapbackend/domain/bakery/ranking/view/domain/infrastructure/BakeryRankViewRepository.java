package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;

/**
 * QueryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface BakeryRankViewRepository {

	List<BakeryRankView> findBakeryRankViewBy(LocalDate calculatedDate, Pageable pageable);

	Optional<BakeryRankView> findByBakeryIdAndCalculatedDate(Long bakeryId, LocalDate calculatedDate);

	BakeryRankView save(BakeryRankView updatedBakeryRankView);

	List<BakeryRankView> findByBakeryIdInAndCalculatedDate(List<Long> bakeryIdList, LocalDate now, Pageable page);

	void saveAll(List<BakeryRankView> updatedBakeryRankView);
 
}
