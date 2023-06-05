package com.depromeet.breadmapbackend.global.security.userinfo;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OIDCUserInfo {

	private final OAuthType oAuthType;
	private final String subject;
	private final String email;

	public String getOAuthId() {
		return this.oAuthType + "_" + this.subject;
	}
}
