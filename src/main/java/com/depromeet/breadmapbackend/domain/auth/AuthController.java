package com.depromeet.breadmapbackend.domain.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.auth.dto.LoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.RegisterRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<JwtToken> login(@RequestBody @Validated(ValidationSequence.class) LoginRequest request) {
		return new ApiResponse<>(authService.login(request));
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<JwtToken> register(@RequestBody @Validated(ValidationSequence.class) RegisterRequest request) {
		return new ApiResponse<>(authService.register(request));
	}

	@PostMapping("/reissue")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) ReissueRequest request) {
		return new ApiResponse<>(authService.reissue(request));
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@RequestBody @Validated(ValidationSequence.class) LogoutRequest request) {
		authService.logout(request);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deRegisterUser(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@RequestBody @Validated(ValidationSequence.class) LogoutRequest request
	) {
		authService.deRegisterUser(request, currentUserInfo.getId());
	}
}
