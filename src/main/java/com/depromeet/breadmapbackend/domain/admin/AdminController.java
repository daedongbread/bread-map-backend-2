package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminJoinRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminLoginRequest;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.domain.admin.dto.*;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.domain.user.dto.ReissueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public void adminJoin(@RequestBody @Validated(ValidationSequence.class) AdminJoinRequest request) {
        adminService.adminJoin(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> adminLogin(@RequestBody @Validated(ValidationSequence.class) AdminLoginRequest request) {
        return new ApiResponse<>(adminService.adminLogin(request));
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JwtToken> reissue(@RequestBody @Validated(ValidationSequence.class) ReissueRequest request) {
        return new ApiResponse<>(adminService.reissue(request));
    }

    @GetMapping("/bar")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminBarDto> getAdminBar() {
        return new ApiResponse<>(adminService.getAdminBar());
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AdminImageDto> uploadImage(@RequestPart MultipartFile image) throws IOException {
        return new ApiResponse<>(adminService.uploadImage(image));
    }
}
