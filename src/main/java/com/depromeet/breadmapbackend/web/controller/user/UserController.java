package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) ReissueRequest request) {
        return new ApiResponse<>(userService.reissue(request));
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProfileDto> profile(@CurrentUser String username, @PathVariable Long userId) {
        return new ApiResponse<>(userService.profile(username, userId));
    }

    @PostMapping("/nickname")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNickName(@CurrentUser String username,
                               @RequestPart @Validated(ValidationSequence.class) UpdateNickNameRequest request,
                               @RequestPart(required = false) MultipartFile file) throws IOException {
        userService.updateNickName(username, request, file);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Validated(ValidationSequence.class) LogoutRequest request) {
        userService.logout(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@CurrentUser String username) {
        userService.deleteUser(username);
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(
            @CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) FollowRequest request) {
        userService.follow(username, request);
    }

    @DeleteMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFollowing(
            @CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) FollowRequest request) {
        userService.unfollow(username, request);
    }

    @GetMapping("/{userId}/follower")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowUserDto>> followerList(@CurrentUser String username, @PathVariable Long userId) {
        return new ApiResponse<>(userService.followerList(username, userId));
    }

    @GetMapping("/{userId}/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowUserDto>> followingList(@CurrentUser String username, @PathVariable Long userId) {
        return new ApiResponse<>(userService.followingList(username, userId));
    }

    @GetMapping("/block")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BlockUserDto>> blockList(@CurrentUser String username) {
        return new ApiResponse<>(userService.blockList(username));
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public void block(@CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        userService.block(username, request);
    }

    @DeleteMapping("/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        userService.unblock(username, request);
    }

    @GetMapping("/alarm")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AlarmDto> getAlarmStatus(@CurrentUser String username) {
        return new ApiResponse<>(userService.getAlarmStatus(username));
    }

    @PatchMapping("/alarm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlarmStatus(@CurrentUser String username) {
        userService.updateAlarmStatus(username);
    }
}
