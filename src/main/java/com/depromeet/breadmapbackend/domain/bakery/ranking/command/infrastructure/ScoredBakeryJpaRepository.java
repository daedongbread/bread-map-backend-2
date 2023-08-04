package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

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
public interface ScoredBakeryJpaRepository extends JpaRepository<ScoredBakeryJpaEntity, Long> {

	@Query("select sb "
		+ "from ScoredBakeryJpaEntity sb "
		+ "join fetch sb.bakery b "
		+ "where sb.calculatedDate = :calculatedDate "
		+ "order by sb.rank asc ")
	List<ScoredBakeryJpaEntity> findScoredBakeryByCalculatedDate(
		@Param("calculatedDate") final LocalDate calculatedDate,
		final Pageable pageable
	);

	@Query("select sb "
		+ "from ScoredBakeryJpaEntity sb "
		+ "join fetch sb.bakery b "
		+ "where sb.calculatedDate = :calculatedDate "
		+ "order by sb.rank asc ")
	List<ScoredBakeryJpaEntity> findScoredBakeryByCalculatedDate(
		@Param("calculatedDate") final LocalDate calculatedDate
	);

	@Query("select sb "
		+ "from ScoredBakeryJpaEntity sb "
		+ "join fetch sb.bakery b "
		+ "where sb.calculatedDate between :startDate and :endDate")
	List<ScoredBakeryJpaEntity> findScoredBakeryWithStartDate(
		@Param("startDate") final LocalDate startDate,
		@Param("endDate") final LocalDate endDate
	);

	void deleteByCalculatedDate(LocalDate calculateDate);
}
