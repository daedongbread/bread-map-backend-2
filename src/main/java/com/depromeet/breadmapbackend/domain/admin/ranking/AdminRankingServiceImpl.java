package com.depromeet.breadmapbackend.domain.admin.ranking;

import java.time.LocalDate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.BakeryRankingCalculationUseCase;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * AdminRankingServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
@Service
@RequiredArgsConstructor
public class AdminRankingServiceImpl implements AdminRankingService {

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final BakeryRankingCalculationUseCase bakeryRankingCalculationUseCase;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final BakeryRankViewRankChangeEvent bakeryRankViewRankChangeEvent;

	@Transactional(readOnly = true)
	@Override
	public RankingResponse getRanking(final String startDateString) {
		return scoredBakeryRepository.findScoredBakeryByStartDate(
			LocalDateParser.parse(startDateString)
		);
	}

	@Transactional
	@Override
	public int updateRanking(final RankingUpdateRequest request) {
		final int updateCount = scoredBakeryRepository.updateRank(request);
		try {
			bakeryRankViewRankChangeEvent.publish(
				EventInfo.BAKERY_RANK_CHANGE_EVENT,
				objectMapper.writeValueAsString(request.bakeryRankInfoList())
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return updateCount;

	}

	@Transactional
	@Override
	public void reCalculateRanking() {
		final String eventName = EventInfo.CALCULATE_BAKERY_RANKING_EVENT.getEventName();
		final LocalDate now = LocalDate.now();
		final String calculateDate = LocalDateParser.parse(now);

		redisTemplate.opsForValue().getAndDelete(eventName + ":" + calculateDate);
		scoredBakeryRepository.deleteByCalculatedDate(now);
		bakeryRankingCalculationUseCase.command(eventName + ":" + calculateDate, now);
	}
}
