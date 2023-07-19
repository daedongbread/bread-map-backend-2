package com.depromeet.breadmapbackend.domain.admin.ranking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryRepository;
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
	}
}
