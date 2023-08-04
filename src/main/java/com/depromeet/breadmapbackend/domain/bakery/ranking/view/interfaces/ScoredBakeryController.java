package com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewUseCase;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@RestController
@RequestMapping("/v1/bakeries/rank")
@RequiredArgsConstructor
public class ScoredBakeryController {

	private final BakeryRankViewUseCase bakeryRankViewUseCase;

	@GetMapping("/{count}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<BakeryRankingCardResponse>> getBakery(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable("count") final int count
	) {
		return new ApiResponse<>(
			Mapper.of(bakeryRankViewUseCase.query(currentUserInfo.getId(), count))
		);
	}
}
