package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) TokenRefreshRequest request) {
        return new ApiResponse<>(userService.reissue(request));
    }
}
