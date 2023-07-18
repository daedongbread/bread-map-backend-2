package com.depromeet.breadmapbackend.domain.admin.ranking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.global.converter.LocalDateTimeParser;

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
	public List<RankingResponse> getRanking(final String startDateString) {
		return scoredBakeryRepository.findScoredBakeryByStartDate(
			LocalDateTimeParser.parse(startDateString)
		);
	}

	@Transactional
	@Override
	public void updateRanking(final RankingUpdateRequest request) {
		scoredBakeryRepository.updateRank(request.bakeryRankInfoList());
	}
}
