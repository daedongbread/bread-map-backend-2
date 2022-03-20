package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("auth/refresh")
    public ApiResponse<JwtToken> refresh(HttpServletRequest request, HttpServletResponse response) {
        JwtToken jwtToken = userService.refresh(request, response);

        return new ApiResponse<>(jwtToken);
    }

}
