package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

//    @DeleteMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteUser(@CurrentUser String oAuthId) {
//        userService.deleteUser(username);
//    }

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
