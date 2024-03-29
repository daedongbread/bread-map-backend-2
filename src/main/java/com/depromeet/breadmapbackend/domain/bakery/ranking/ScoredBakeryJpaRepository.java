package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
		+ "join fetch sb.bakery b "
		+ "where sb.calculatedDate = :calculatedDate "
		+ "and b.status = 'POSTING' "
		+ "order by sb.rank asc ")
	List<ScoredBakery> findScoredBakeryByCalculatedDate(
		@Param("calculatedDate") final LocalDate calculatedDate,
		final Pageable pageable
	);

	@Query("select sb "
		+ "from ScoredBakery sb "
		+ "join fetch sb.bakery b "
		+ "where sb.calculatedDate between :startDate and :endDate "
		+ "and b.status = 'POSTING' ")
	List<ScoredBakery> findScoredBakeryWithStartDate(
		@Param("startDate") final LocalDate startDate,
		@Param("endDate") final LocalDate endDate
	);

	void deleteByCalculatedDate(LocalDate calculateDate);
}
