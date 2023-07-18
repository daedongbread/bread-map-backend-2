package com.depromeet.breadmapbackend.domain.admin.ranking;

import java.util.List;

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
	List<RankingResponse> getRanking(String startDateString);

	void updateRanking(RankingUpdateRequest request);
}
