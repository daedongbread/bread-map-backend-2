package com.depromeet.breadmapbackend.security.token;

import com.depromeet.breadmapbackend.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    private static final String AUTHORITIES_KEY = "role";

    public JwtTokenProvider(AppProperties appProperties) {
        this.key = Keys.hmacShaKeyFor(appProperties.getTokenSecret().getBytes());
        accessTokenExpiry = appProperties.getAccessTokenExpiry();
        refreshTokenExpiry = appProperties.getRefreshTokenExpiry();
    }

    public JwtToken createJwtToken(String username, String role) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + accessTokenExpiry))
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiry))
                .compact();

        return new JwtToken(accessToken, refreshToken);
    }

    // 만료된 토큰이거나 다른 에러가 발생한다면 false
    public boolean verifyToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        if(verifyToken(token)) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserPrincipal userPrincipal = UserPrincipal.create(claims.getSubject(), authorities);

            return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
        } else {
            throw new TokenValidFailedException();
        }
    }

}
