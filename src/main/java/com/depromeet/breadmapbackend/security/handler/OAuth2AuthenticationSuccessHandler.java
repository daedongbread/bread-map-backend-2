package com.depromeet.breadmapbackend.security.handler;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserStatus;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.domain.UserPrincipal;
import com.depromeet.breadmapbackend.security.exception.UserBlockException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
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

        String username = userPrincipal.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(user.getStatus().equals(UserStatus.BLOCK)) throw new UserBlockException();
        RoleType roleType = hasAuthority(userPrincipal.getAuthorities(), RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        JwtToken jwtToken = jwtTokenProvider.createJwtToken(username, roleType.getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + ":" + username,
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