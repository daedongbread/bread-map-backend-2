package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
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
    public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) TokenRequest request) {
        return new ApiResponse<>(userService.reissue(request));
    }

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProfileDto> profile(@CurrentUser String username) {
        return new ApiResponse<>(userService.profile(username));
    }

    @PatchMapping("/nickname")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNickName(@CurrentUser String username,
                               @RequestBody @Validated(ValidationSequence.class) UpdateNickNameRequest request) {
        userService.updateNickName(username, request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Validated(ValidationSequence.class) TokenRequest request) {
        userService.logout(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@CurrentUser String username) {
        userService.deleteUser(username);
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
    public ApiResponse<List<SimpleUserDto>> followerList(@CurrentUser String username) {
        return new ApiResponse<>(userService.followerList(username));
    }

    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SimpleUserDto>> followingList(@CurrentUser String username) {
        return new ApiResponse<>(userService.followingList(username));
    }

    @GetMapping("/block")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<SimpleUserDto>> blockList(@CurrentUser String username) {
        return new ApiResponse<>(userService.blockList(username));
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public void block(@CurrentUser String username, @RequestBody BlockRequest request) {
        userService.block(username, request);
    }

    @DeleteMapping("/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@CurrentUser String username, @RequestBody BlockRequest request) {
        userService.unblock(username, request);
    }
}
