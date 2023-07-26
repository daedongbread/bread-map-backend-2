package com.depromeet.breadmapbackend.domain.admin.ranking;

import java.time.LocalDate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.BakeryRankingScheduler;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AdminRankingController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
@Slf4j
@RequestMapping("/v1/admin/rank")
@RequiredArgsConstructor
@RestController
public class AdminRankingController {

	private final AdminRankingService adminRankingService;

	@GetMapping("/{startDate}")
	ApiResponse<RankingResponse> getRanking(@PathVariable("startDate") String startDate) {
		return new ApiResponse<>(adminRankingService.getRanking(startDate));
	}

	@PostMapping
	ApiResponse<Integer> updateRanking(@RequestBody RankingUpdateRequest request) {
		return new ApiResponse<>(adminRankingService.updateRanking(request));
	}

	@PatchMapping
	void reCalculateRanking() {
		adminRankingService.reCalculateRanking();
	}

}
