package com.depromeet.breadmapbackend.domain.admin.ranking;

import java.time.LocalDate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;

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
	private final BakeryRankingScheduler bakeryRankingScheduler;
	private final StringRedisTemplate redisTemplate;

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
		return scoredBakeryRepository.updateRank(request);
		// TODO : rank view 변경 이벤트 발행 -> ( 랭킹뷰 재생성  기본적인 빵집 정보만 저장 )
	}

	@Transactional
	@Override
	public void reCalculateRanking() {
		final String eventName = EventInfo.CALCULATE_RANKING_EVENT.getEventName();
		final LocalDate now = LocalDate.now();
		final String calculateDate = LocalDateParser.parse(now);

		redisTemplate.opsForValue().getAndDelete(eventName + ":" + calculateDate);
		scoredBakeryRepository.deleteByCalculatedDate(now);
		bakeryRankingScheduler.publishBakeryRankingCalculationEvent();
	}
}
