package com.depromeet.breadmapbackend.domain.admin.user.dto;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminUserDto {
    private Long userId;
    private String userOAuthId;
    private String nickName;
    private String email;
    private String createdAt;
    private String lastAccessAt;
    private RoleType roleType;

    @Builder
    public AdminUserDto(User user) {
        this.userId = user.getId();
        this.userOAuthId = user.getOAuthId();
        this.nickName = user.getUserInfo().getNickName();
        this.email = user.getUserInfo().getEmail();
        this.createdAt = user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.lastAccessAt = user.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.roleType = user.getRoleType();
    }
}
