package com.depromeet.breadmapbackend.security.token;

import com.depromeet.breadmapbackend.security.CAuthenticationEntryPointException;
import com.depromeet.breadmapbackend.security.domain.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String key;
    private final Long accessTokenExpiredDate = 60 * 60 * 1000L; // 1 hour/
    private final Long refreshTokenExpiredDate = 14 * 24 * 60 * 60 * 1000L; // 14 day

    private static final String ROLES = "roles";

//    @PostConstruct
//    protected void init() {
//        this.key = Base64UrlCodec.BASE64URL.encode(key.getBytes(StandardCharsets.UTF_8));
//    }

    private Key getSigninKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken createJwtToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES, role);

        Date now = new Date();
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiredDate))
                .signWith(getSigninKey(key), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiredDate))
                .signWith(getSigninKey(key), SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .accessTokenExpiredDate(accessTokenExpiredDate).build();
    }

    // ????????? ??????????????? ?????? ????????? ??????????????? false
    public boolean verifyToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigninKey(key))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().after(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            log.error("????????? Jwt ???????????????.");
        } catch (ExpiredJwtException e) {
            log.error("????????? ???????????????.");
        } catch (UnsupportedJwtException e) {
            log.error("???????????? ?????? ???????????????.");
        } catch (IllegalArgumentException e) {
            log.error("????????? ???????????????.");
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigninKey(key)).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // ?????? ????????? ??????
        if (claims.get(ROLES) == null) {
            throw new CAuthenticationEntryPointException();
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(ROLES).toString()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserPrincipal userPrincipal = UserPrincipal.create(claims.getSubject(), authorities);

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }
}
