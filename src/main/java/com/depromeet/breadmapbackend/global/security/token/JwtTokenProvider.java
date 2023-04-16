package com.depromeet.breadmapbackend.global.security.token;

import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.global.infra.properties.CustomJWTKeyProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CustomJWTKeyProperties customJWTKeyProperties;
    private final Long accessTokenExpiredDate = 60 * 60 * 1000L; // 1 hours
    private final Long refreshTokenExpiredDate = 14 * 24 * 60 * 60 * 1000L; // 14 days

    private static final String ROLES = "roles";

//    @PostConstruct
//    protected void init() {
//        this.key = Base64.getEncoder()
//                .encodeToString(key.getBytes(StandardCharsets.UTF_8));
//    }

    private Key getSigningKey() {
        byte[] keyBytes = customJWTKeyProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken createJwtToken(String oAuthId, String role) {
        Long id = null;
        if (role.equals(RoleType.USER.getCode())) {
            id = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND)).getId();
        } else if (role.equals(RoleType.ADMIN.getCode())) {
            id = adminRepository.findByEmail(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND)).getId();
        }

        Claims claims = Jwts.claims().setSubject(oAuthId);
        claims.put(ROLES, role);

        Date now = new Date();
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiredDate))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiredDate))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder().userId(id)
                .accessToken(accessToken).refreshToken(refreshToken)
                .accessTokenExpiredDate(accessTokenExpiredDate).build();
    }

    public JwtToken createTestJwtToken(String oAuthId, String role) {
        Long id = adminRepository.findByEmail(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND)).getId();

        Claims claims = Jwts.claims().setSubject(oAuthId);
        claims.put(ROLES, role);

        Date now = new Date();
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 60 * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiredDate))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder().userId(id)
                .accessToken(accessToken).refreshToken(refreshToken)
                .accessTokenExpiredDate(accessTokenExpiredDate).build();
    }

    // 만료된 토큰이거나 다른 에러가 발생한다면 false
    public boolean verifyToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private Claims parseClaimsIgnoringExpiration(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Authentication getAuthentication(String token, boolean unexpired) {
        Claims claims;
        if (unexpired) claims = parseClaims(token);
        else claims = parseClaimsIgnoringExpiration(token);

        // 권한 정보가 없음
        if (claims.get(ROLES) == null) {
            throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(ROLES).toString()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserPrincipal userPrincipal = UserPrincipal.create(claims.getSubject(), authorities); // TODO

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }

    public Long getRefreshTokenExpiredDate() {
        return this.refreshTokenExpiredDate;
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = parseClaims(accessToken).getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
