package com.depromeet.breadmapbackend.domain.admin.user;

import com.depromeet.breadmapbackend.domain.admin.user.dto.AdminUserDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    PageResponseDto<AdminUserDto> getUserList(int page);
    void changeUserBlock(Long userId);
}
