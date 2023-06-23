package com.depromeet.breadmapbackend.domain.bakery;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/bakeries")
@RequiredArgsConstructor
public class BakeryController {
	private final BakeryService bakeryService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<BakeryCardDto>> getBakeryList(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@RequestParam @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class) BakerySortType sortBy,
		@RequestParam Boolean filterBy,
		@RequestParam Double latitude, @RequestParam Double longitude,
		@RequestParam Double latitudeDelta, @RequestParam Double longitudeDelta) {
		return new ApiResponse<>(
			bakeryService.getBakeryList(
				currentUserInfo.getId(),
				sortBy,
				filterBy,
				latitude,
				longitude,
				latitudeDelta,
				longitudeDelta
			)
		);
	}

	@GetMapping("/{bakeryId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<BakeryDto> getBakery(@CurrentUser String oAuthId, @PathVariable Long bakeryId) {
		return new ApiResponse<>(bakeryService.getBakery(oAuthId, bakeryId));
	}
}
