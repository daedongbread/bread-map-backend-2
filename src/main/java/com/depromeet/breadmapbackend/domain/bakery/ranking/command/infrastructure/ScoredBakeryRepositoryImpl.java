package com.depromeet.breadmapbackend.domain.bakery.ranking.command.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScoredBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Slf4j
@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ScoredBakeryRepositoryImpl implements ScoredBakeryRepository {

	private static final String TABLE = "scored_bakery";
	private final ScoredBakeryJpaRepository scoredBakeryJpaRepository;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Transactional
	public int bulkInsert(final List<ScoredBakery> scoredBakeryList) {
		log.info("bulk insert scored bakery list: {}", scoredBakeryList.size());
		String sql = String.format(
			"""
					INSERT INTO `%s` (bakery_id, total_score, view_count, rating, flag_count, calculated_date, bakery_rank)
					VALUES (:bakery, :totalScore, :viewCount, :rating, :flagCount, :calculatedDate, :bakery_rank)
				""", TABLE);

		SqlParameterSource[] params = scoredBakeryList.stream()
			.map(scoredBakery -> new MapSqlParameterSource()
				.addValue("bakery", scoredBakery.bakery().getId())
				.addValue("totalScore", scoredBakery.totalScore())
				.addValue("viewCount", scoredBakery.viewCount())
				.addValue("flagCount", scoredBakery.flagCount())
				.addValue("rating", scoredBakery.rating())
				.addValue("calculatedDate", scoredBakery.calculatedDate())
				.addValue("bakery_rank", scoredBakery.rank())
			)
			.toArray(SqlParameterSource[]::new);
		return namedParameterJdbcTemplate.batchUpdate(sql, params).length;
	}

	@Override
	public RankingResponse findScoredBakeryByStartDate(final LocalDate startDate) {
		final LocalDate endDate = startDate.plusDays(6);
		final List<ScoredBakeryJpaEntity> bakeryRanking =
			scoredBakeryJpaRepository.findScoredBakeryWithStartDate(startDate, endDate);

		if (!bakeryRanking.isEmpty()) {
			final List<String> dateList = bakeryRanking.stream()
				.map(ScoredBakeryJpaEntity::getCalculatedDate)
				.distinct()
				.sorted()
				.map(LocalDate::toString)
				.toList();

			return new RankingResponse(
				LocalDateParser.parse(startDate),
				LocalDateParser.parse(endDate),
				dateList,
				bakeryRanking.stream().map(RankingResponse.SimpleBakeryResponse::new).toList()
			);
		}
		return new RankingResponse(
			LocalDateParser.parse(startDate),
			LocalDateParser.parse(endDate),
			List.of(),
			List.of()
		);
	}

	@Transactional
	@Override
	public int updateRank(final RankingUpdateRequest request) {

		String sql = String.format(
			"""
					UPDATE `%s` SET bakery_rank = :bakery_rank
					WHERE id = :id
				""", TABLE);

		final List<RankingUpdateRequest.BakeryRankInfo> bakeryRankInfos = request.bakeryRankInfoList();

		SqlParameterSource[] params = bakeryRankInfos.stream()
			.map(rankInfo -> new MapSqlParameterSource()
				.addValue("bakery_rank", rankInfo.rank())
				.addValue("id", rankInfo.id())
			)
			.toArray(SqlParameterSource[]::new);
		return namedParameterJdbcTemplate.batchUpdate(sql, params).length;
	}

	@Override
	public void deleteByCalculatedDate(final LocalDate calculateDate) {
		log.info("bulk delete scored bakery list");

		String sql = String.format(
			"""
					delete
					from `%s` 
					where calculated_date = :calculatedDate 
				""", TABLE);

		SqlParameterSource param = new MapSqlParameterSource()
			.addValue("calculatedDate", LocalDateParser.parse(calculateDate));
		namedParameterJdbcTemplate.update(sql, param);
	}
}
