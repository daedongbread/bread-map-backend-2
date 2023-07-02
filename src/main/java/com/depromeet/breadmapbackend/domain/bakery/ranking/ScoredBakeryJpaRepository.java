package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * ScoredBakeryJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryJpaRepository extends JpaRepository<ScoredBakery, Long> {

	@Query("select sb "
		+ "from ScoredBakery sb "
		+ "order by sb.totalScore desc, sb.bakery.id desc")
	List<ScoredBakery> findBakeriesRankTop(final Pageable pageable);
}
