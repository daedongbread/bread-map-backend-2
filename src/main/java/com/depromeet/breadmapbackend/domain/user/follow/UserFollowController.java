package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowUserDto;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService userFollowService;

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(
            @CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) FollowRequest request) {
        userFollowService.follow(oAuthId, request);
    }

    @DeleteMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFollowing(
            @CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) FollowRequest request) {
        userFollowService.unfollow(oAuthId, request);
    }

    @GetMapping("/{userId}/follower")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowUserDto>> followerList(@CurrentUser String oAuthId, @PathVariable Long userId) {
        return new ApiResponse<>(userFollowService.followerList(oAuthId, userId));
    }

    @GetMapping("/{userId}/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<FollowUserDto>> followingList(@CurrentUser String oAuthId, @PathVariable Long userId) {
        return new ApiResponse<>(userFollowService.followingList(oAuthId, userId));
    }
}
