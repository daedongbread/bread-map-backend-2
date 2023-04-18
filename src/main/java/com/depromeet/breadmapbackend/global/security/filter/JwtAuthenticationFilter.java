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
import java.io.InputStream;
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

        if (!request.getRequestURI().equals("/v1/actuator/health")) {
            request = requestLog(request);
        }

        if (request.getHeader("Authorization") != null) {
            String jwtHeader = request.getHeader("Authorization");
            if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
                String accessToken = jwtHeader.replace("Bearer ", "");
                if(jwtTokenProvider.verifyToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken, true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private HttpServletRequest requestLog(HttpServletRequest request) throws IOException {
        // [Method] : endpoint
        log.info("[" + request.getMethod() + "] : " + request.getRequestURI());
        // query :
        if (request.getQueryString() != null) log.info("query : " + request.getQueryString());

        // request body :
        String requestBody = readBody(request);
        if (!requestBody.isEmpty()) {
            log.info("request body : ");
            log.info(requestBody);
            request = new BufferedRequestWrapper(request, requestBody);
        }
        return request;
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try(InputStream inputStream = request.getInputStream()) {
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }
}
