package com.depromeet.breadmapbackend.security.filter;

import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.depromeet.breadmapbackend.security.util.HeaderConstant.HEADER_AUTHORIZATION;
import static com.depromeet.breadmapbackend.security.util.HeaderConstant.TOKEN_PREFIX;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(HEADER_AUTHORIZATION) != null) {
            String jwtHeader = request.getHeader(HEADER_AUTHORIZATION);

            if (jwtHeader != null || jwtHeader.startsWith(TOKEN_PREFIX)) {
                String accessToken = jwtHeader.replace(TOKEN_PREFIX, "");

                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
