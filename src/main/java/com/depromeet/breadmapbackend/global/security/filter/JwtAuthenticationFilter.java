package com.depromeet.breadmapbackend.global.security.filter;

import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
        this.jwtTokenProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().equals("/actuator/health")) {
            log.info("[" + request.getMethod() + "] : " + request.getRequestURI());
            if (request.getQueryString() != null) log.info("query : " + request.getQueryString());
        }
//        if(!readBody(request).equals("")) log.info("request body : " + readBody(request)); // TODO : request body

        if (request.getHeader("Authorization") != null && !request.getRequestURI().startsWith("/exception/")) {
            String jwtHeader = request.getHeader("Authorization");
            if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
                String accessToken = jwtHeader.replace("Bearer ", "");
                if(jwtTokenProvider.verifyToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken, true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        log.info("C");
        filterChain.doFilter(request, response);
    }

    public static String readBody(HttpServletRequest request) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String buffer;
        while ((buffer = input.readLine()) != null) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(buffer);
        }
        return builder.toString();
    }
}
