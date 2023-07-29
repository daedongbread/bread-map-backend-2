package com.depromeet.breadmapbackend.global.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final String TOKEN_PREFIX = "Bearer ";
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader != null) {
			if (authorizationHeader.startsWith(TOKEN_PREFIX)) {
				String accessToken = authorizationHeader.replace(TOKEN_PREFIX, "");
				if (jwtTokenProvider.verifyToken(accessToken)) {
					Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setCharacterEncoding(StandardCharsets.UTF_8.name());
					response.getWriter().write(DaedongStatus.TOKEN_INVALID_EXCEPTION.getDescription());
					return;
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
