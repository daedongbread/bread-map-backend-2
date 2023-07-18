package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;

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

	public int bulkInsert(final List<ScoredBakery> scoredBakeryList) {

		String sql = String.format(
			"""
					INSERT INTO `%s` (bakery_id, total_score, view_count, flag_count, calculated_date)
					VALUES (:bakery, :totalScore, :viewCount, :flagCount, :calculatedDate)
				""", TABLE);

		SqlParameterSource[] params = scoredBakeryList.stream()
			.map(scoredBakery -> new MapSqlParameterSource()
				.addValue("bakery", scoredBakery.getBakery().getId())
				.addValue("totalScore", scoredBakery.getTotalScore())
				.addValue("viewCount", scoredBakery.getViewCount())
				.addValue("flagCount", scoredBakery.getFlagCount())
				.addValue("calculatedDate", scoredBakery.getCalculatedDate())
			)
			.toArray(SqlParameterSource[]::new);
		return namedParameterJdbcTemplate.batchUpdate(sql, params).length;
	}

	@Override
	public List<ScoredBakery> findScoredBakeryByCalculatedDate(final LocalDate calculatedDate, final int size) {
		return scoredBakeryJpaRepository.findScoredBakeryByCalculatedDate(calculatedDate, Pageable.ofSize(size));
	}

	@Override
	public List<RankingResponse> findScoredBakeryByStartDate(final LocalDate startDate) {
		final Pageable pageable = Pageable.ofSize(7);
		return scoredBakeryJpaRepository.findScoredBakeryWithStartDate(startDate, pageable);
	}

	@Override
	public void updateRank(final List<RankingUpdateRequest.BakeryRankInfo> bakeryRankInfos) {

	}
}
