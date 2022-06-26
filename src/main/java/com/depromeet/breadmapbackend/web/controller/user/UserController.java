package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.user.dto.FollowDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.FollowRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.ProfileDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProfileDto> profile(@CurrentUser String username) {
        return new ApiResponse<>(userService.profile(username));
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(@CurrentUser String username, @RequestBody FollowRequest request) {
        userService.follow(username, request);
    }

    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@CurrentUser String username, @RequestBody FollowRequest request) {
        userService.unfollow(username, request);
    }

    @GetMapping("/follower")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowDto>> followerList(@CurrentUser String username) {
        return new ApiResponse<>(userService.followerList(username));
    }

    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowDto>> followingList(@CurrentUser String username) {
        return new ApiResponse<>(userService.followingList(username));
    }
}
