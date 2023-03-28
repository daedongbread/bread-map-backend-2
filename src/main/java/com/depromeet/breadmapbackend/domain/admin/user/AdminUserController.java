package com.depromeet.breadmapbackend.domain.admin.user;

import com.depromeet.breadmapbackend.domain.admin.user.dto.AdminUserDto;
import com.depromeet.breadmapbackend.global.converter.PageableSortConverter;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<AdminUserDto>> getUserList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(adminUserService.getUserList(PageableSortConverter.convertSort(pageable)));
    }

    @PatchMapping("/{userId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserBlock(@PathVariable Long userId) {
        adminUserService.changeUserBlock(userId);
    }
}
