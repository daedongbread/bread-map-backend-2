package com.depromeet.breadmapbackend.security.handler;

import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.security.token.RefreshToken;
import com.depromeet.breadmapbackend.security.token.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final String REDIS_KEY_REFRESH;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String username = userPrincipal.getUsername();
        RoleType roleType = hasAuthority(userPrincipal.getAuthorities(), RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        JwtToken jwtToken = jwtTokenProvider.createJwtToken(username, roleType.getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + username,
                        jwtToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
//        if(refreshTokenRepository.findByUsername(username).isPresent()) {
//            refreshTokenRepository.deleteByUsername(username);
//        }
//        RefreshToken refreshToken = RefreshToken.builder().username(username).token(jwtToken.getRefreshToken()).build();
//        refreshTokenRepository.save(refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(jwtToken));
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

}