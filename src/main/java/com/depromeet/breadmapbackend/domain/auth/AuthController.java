package com.depromeet.breadmapbackend.domain.auth;

import com.depromeet.breadmapbackend.domain.auth.dto.LoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.RegisterRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/valid")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Boolean> checkToLoginOrRegister(@RequestBody @Validated(ValidationSequence.class) LoginRequest request) {
        return new ApiResponse<>(authService.checkToLoginOrRegister(request));
    }

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
}
