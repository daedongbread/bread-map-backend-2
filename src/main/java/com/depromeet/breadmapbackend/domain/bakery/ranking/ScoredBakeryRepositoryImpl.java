package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public int bulkInsert(List<ScoredBakery> scoredBakeryList) {

		String sql = String.format(
			"""
					INSERT INTO `%s` (bakery_id, bakery_rating, flag_count, total_score)
					VALUES (:bakery, :bakeryRating, :flagCount, :totalScore)
				""", TABLE);

		SqlParameterSource[] params = scoredBakeryList.stream()
			.map(scoredBakery -> new MapSqlParameterSource()
				.addValue("bakery", scoredBakery.getBakery().getId())
				.addValue("bakeryRating", scoredBakery.getBakeryRating())
				.addValue("flagCount", scoredBakery.getFlagCount())
				.addValue("totalScore", scoredBakery.getTotalScore()))
			.toArray(SqlParameterSource[]::new);
		return namedParameterJdbcTemplate.batchUpdate(sql, params).length;
	}

	@Override
	public List<ScoredBakery> findBakeriesRankTop(final int count) {
		return scoredBakeryJpaRepository.findBakeriesRankTop(Pageable.ofSize(count));
	}
}
