package com.depromeet.breadmapbackend.global.security.userinfo;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;

import lombok.Getter;

@Getter
public class OICDUserInfo {

	private final OAuthType oAuthType;
	private final String subject;
	private final String email;

	public OICDUserInfo(final OAuthType oAuthType, final String subject, final String email) {
		this.oAuthType = oAuthType;
		this.subject = subject;
		this.email = email;
	}

	public String getOAuthId() {
		return this.oAuthType + "_" + this.subject;
	}
}
