package com.depromeet.breadmapbackend.domain.flag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/flags")
@RequiredArgsConstructor
public class FlagController {
	private final FlagService flagService;

	@GetMapping("/users/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<FlagDto>> getFlags(@PathVariable Long userId) {
		return new ApiResponse<>(flagService.getFlags(userId));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addFlag(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@RequestBody @Validated(ValidationSequence.class) FlagRequest request
	) {
		flagService.addFlag(currentUserInfo.getId(), request);
	}

	@PatchMapping("/{flagId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateFlag(
		@PathVariable Long flagId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@RequestBody @Validated(ValidationSequence.class) FlagRequest request
	) {
		flagService.updateFlag(currentUserInfo.getId(), flagId, request);
	}

	@DeleteMapping("/{flagId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeFlag(
		@PathVariable Long flagId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		flagService.removeFlag(currentUserInfo.getId(), flagId);
	}

	@GetMapping("/{flagId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<FlagBakeryDto> getBakeryByFlag(
		@PathVariable Long flagId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		return new ApiResponse<>(flagService.getBakeryByFlag(currentUserInfo.getId(), flagId));
	}

	@PostMapping("/{flagId}/bakeries/{bakeryId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addBakeryToFlag(
		@PathVariable Long flagId,
		@PathVariable Long bakeryId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		flagService.addBakeryToFlag(currentUserInfo.getId(), flagId, bakeryId);
	}

	@DeleteMapping("/{flagId}/bakeries/{bakeryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeBakeryToFlag(
		@PathVariable Long flagId,
		@PathVariable Long bakeryId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		flagService.removeBakeryToFlag(currentUserInfo.getId(), flagId, bakeryId);
	}
}
