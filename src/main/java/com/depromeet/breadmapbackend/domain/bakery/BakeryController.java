package com.depromeet.breadmapbackend.domain.bakery;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
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
		@Valid final BakeryListRequest bakeryListRequest
	) {
		return new ApiResponse<>(
			bakeryService.getBakeryList(
				currentUserInfo.getId(),
				bakeryListRequest.sortBy(),
				bakeryListRequest.filterBy(),
				bakeryListRequest.latitude(),
				bakeryListRequest.longitude(),
				bakeryListRequest.latitudeDelta(),
				bakeryListRequest.longitudeDelta()
			)
		);
	}

	@GetMapping("/{bakeryId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<BakeryDto> getBakery(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long bakeryId
	) {
		return new ApiResponse<>(bakeryService.getBakery(currentUserInfo.getId(), bakeryId));
	}
}
