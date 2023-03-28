package com.depromeet.breadmapbackend.global.security.domain;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User, OidcUser {

    private final String username;
    private final ProviderType providerType;
    private final RoleType roleType;

    // 부가적인 정보들
    private final Collection<? extends GrantedAuthority> authorities; // roleType 을 Authority 타입으로 바꾼 것. ( 우리 서버에서는 role 이 계정 당 1 개이므로 컬렉션의 사이즈는 1 )
    private final Map<String, Object> attributes; // OAuth2User 에서 필요. 여기에는 OAuth 서버에서 받은 속성들이 들어있다.

    private UserPrincipal(String username, ProviderType providerType, RoleType roleType, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.username = username;
        this.providerType = providerType;
        this.roleType = roleType;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    } // oauth2 에서 필요

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public String getName() {
        return username;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getUsername(),
                user.getProviderType(),
                RoleType.USER,
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode())),
                null
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) { // TODO
        return new UserPrincipal(
                user.getUsername(),
                user.getProviderType(),
                RoleType.USER,
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode())),
                attributes
        );
    }

    public static UserPrincipal create(String username, Collection<? extends GrantedAuthority> authorities) { // TODO
        return new UserPrincipal(
                username,
                null,
                RoleType.USER,
                authorities,
                null
        );
    }

}
