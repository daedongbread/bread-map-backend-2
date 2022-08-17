package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminUserDto {
    private Long id;
    private String username;
    private String nickName;
    private String email;
    private String createdAt;
    private String lastAccessAt;
    private RoleType roleType;

    @Builder
    public AdminUserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.lastAccessAt = user.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.roleType = user.getRoleType();
    }
}
