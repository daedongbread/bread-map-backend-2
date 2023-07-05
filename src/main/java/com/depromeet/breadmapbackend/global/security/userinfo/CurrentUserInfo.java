package com.depromeet.breadmapbackend.global.security.userinfo;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurrentUserInfo {

	private Long id;
	private String delimiterValue;
	private RoleType roleType;

	public CurrentUserInfo(final Long id, final String delimiterValue, final RoleType roleType) {
		this.id = id;
		this.delimiterValue = delimiterValue;
		this.roleType = roleType;
	}

	public static CurrentUserInfo of(User user) {
		return new CurrentUserInfo(user.getId(), user.getOAuthId(), user.getRoleType());
	}

	public static CurrentUserInfo of(Admin admin) {
		return new CurrentUserInfo(admin.getId(), admin.getEmail(), admin.getRoleType());
	}
}
