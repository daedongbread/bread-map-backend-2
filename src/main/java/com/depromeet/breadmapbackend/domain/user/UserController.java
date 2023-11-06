package com.depromeet.breadmapbackend.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.user.dto.AlarmDto;
import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.user.dto.ProfileDto;
import com.depromeet.breadmapbackend.domain.user.dto.UpdateNickNameRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ProfileDto> profile(@CurrentUser String oAuthId, @PathVariable Long userId) {
		return new ApiResponse<>(userService.profile(oAuthId, userId));
	}

	@PostMapping("/nickname")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNickName(
		@CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) UpdateNickNameRequest request) {
		userService.updateNickName(oAuthId, request);
	}

	@GetMapping("/alarm")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<AlarmDto> getAlarmStatus(@CurrentUser String oAuthId) {
		return new ApiResponse<>(userService.getAlarmStatus(oAuthId));
	}

	@PatchMapping("/alarm")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ApiResponse<AlarmDto> alarmChange(
		@CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) NoticeTokenRequest request) {
		return new ApiResponse<>(userService.alarmChange(oAuthId, request));
	}
}
