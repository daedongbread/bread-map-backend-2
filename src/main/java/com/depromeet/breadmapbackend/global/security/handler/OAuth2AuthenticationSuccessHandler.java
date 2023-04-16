package com.depromeet.breadmapbackend.global.security.handler;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String oAuthId = userPrincipal.getOAuthId();
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(user.getIsBlock()) throw new DaedongException(DaedongStatus.BLOCK_USER);
        RoleType roleType = hasAuthority(userPrincipal.getAuthorities(), RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        JwtToken jwtToken = jwtTokenProvider.createJwtToken(oAuthId, roleType.getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + ":" + oAuthId,
                        jwtToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        ObjectMapper objectMapper = new ObjectMapper();
        var writer = response.getWriter();

        writer.println("<script>setTimeout(function() {");
        writer.println("window.ReactNativeWebView.postMessage('" + objectMapper.writeValueAsString(jwtToken) + "');");
        writer.println("}, 500);");
        writer.println("</script>");
        writer.flush();

//        ObjectMapper objectMapper = new ObjectMapper();
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(jwtToken));
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