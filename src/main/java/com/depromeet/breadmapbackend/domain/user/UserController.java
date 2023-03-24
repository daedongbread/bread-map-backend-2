package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
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
    public void updateNickName(
            @CurrentUser String username,
            @RequestPart @Validated(ValidationSequence.class) UpdateNickNameRequest request,
            @RequestPart(required = false) MultipartFile file) throws IOException {
        userService.updateNickName(username, request, file);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Validated(ValidationSequence.class) LogoutRequest request) {
        userService.logout(request);
    }

//    @DeleteMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteUser(@CurrentUser String username) {
//        userService.deleteUser(username);
//    }

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
