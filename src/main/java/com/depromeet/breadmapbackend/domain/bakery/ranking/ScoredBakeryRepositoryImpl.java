package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Repository
@RequiredArgsConstructor
public class ScoredBakeryRepositoryImpl implements ScoredBakeryRepository {

	private static final String TABLE = "scored_bakery";
	private final ScoredBakeryJpaRepository scoredBakeryJpaRepository;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final ScoredBakeryCacheRepository scoredBakeryCacheRepository;

	public int bulkInsert(final List<ScoredBakery> scoredBakeryList, final String yearWeekOfMonth) {

		String sql = String.format(
			"""
					INSERT INTO `%s` (bakery_id, bakery_rating, flag_count, total_score, created_week_of_month_year)
					VALUES (:bakery, :bakeryRating, :flagCount, :totalScore, :createdWeekOfMonthYear)
				""", TABLE);

		SqlParameterSource[] params = scoredBakeryList.stream()
			.map(scoredBakery -> new MapSqlParameterSource()
				.addValue("bakery", scoredBakery.getBakery().getId())
				.addValue("bakeryRating", scoredBakery.getBakeryRating())
				.addValue("flagCount", scoredBakery.getFlagCount())
				.addValue("totalScore", scoredBakery.getTotalScore())
				.addValue("createdWeekOfMonthYear", yearWeekOfMonth)
			)
			.toArray(SqlParameterSource[]::new);
		return namedParameterJdbcTemplate.batchUpdate(sql, params).length;
	}

	@Override
	public List<ScoredBakery> findCachedScoredBakeryByWeekOfMonthYear(final String weekOfMonthYear, final int size) {
		return scoredBakeryCacheRepository.findScoredBakeryByWeekOfMonthYear(weekOfMonthYear, size);
	}

	@Override
	public List<ScoredBakery> findScoredBakeryByWeekOfMonthYear(final String weekOfMonthYear, final int size) {
		return scoredBakeryJpaRepository.findScoredBakeryByWeekOfMonthYear(weekOfMonthYear, Pageable.ofSize(size));
	}
}
