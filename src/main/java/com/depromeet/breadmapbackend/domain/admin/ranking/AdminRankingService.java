package com.depromeet.breadmapbackend.domain.admin.ranking;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;

/**
 * AdminRankingService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
public interface AdminRankingService {
	RankingResponse getRanking(String startDateString);

	int updateRanking(RankingUpdateRequest request);

	void reCalculateRanking();

}
