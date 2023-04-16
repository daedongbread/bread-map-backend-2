package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthInfo {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "oauth_type")
    private OAuthType oAuthType;

    @Column(nullable = false, name = "oauth_id")
    private String oAuthId;
}
