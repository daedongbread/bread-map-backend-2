package com.depromeet.breadmapbackend.global.infra;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.depromeet.breadmapbackend.global.infra.filter.LoggingFilter;
import com.depromeet.breadmapbackend.global.security.CustomAccessDeniedHandler;
import com.depromeet.breadmapbackend.global.security.CustomAuthenticationEntryPoint;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.filter.JwtAuthenticationFilter;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final LoggingFilter loggingFilter;
	private final ObjectMapper objectMapper;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors().configurationSource(corsConfigurationSource())
			.and()
			.httpBasic().disable()
			.formLogin().disable()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/v1/auth/valid", "/v1/auth/login", "/v1/auth/register", "/v1/auth/reissue",
				"/v1/exception/**").permitAll()
			.antMatchers("/v1/admin/join", "/v1/admin/login", "/v1/admin/reissue", "/v1/admin/test").permitAll()
			.antMatchers("/h2-console/**", "/favicon.ico", "/v1/actuator/health").permitAll()
			.antMatchers("/v1/bakeries/**", "/v1/flags/**", "/v1/reviews/**", "/v1/users/**", "/v1/notices/**",
				"/v1/search/**", "/v1/images/**", "/v1/auth/**", "/v1/posts/**").hasAuthority(RoleType.USER.getCode())
			.antMatchers("/v1/admin/**").hasAuthority(RoleType.ADMIN.getCode())
			//                .antMatchers("/**").hasAnyAuthority(RoleType.USER.getCode())
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			//                .anyRequest().denyAll()
			//                .anyRequest().authenticated()
			// 설정 시
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(customAuthenticationEntryPoint)
			.accessDeniedHandler(customAccessDeniedHandler)
			.and()
			.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, objectMapper),
				UsernamePasswordAuthenticationFilter.class);

	}

	// Cors 설정
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		//        configuration.addAllowedOrigin("http://localhost:3000");
		//        configuration.addAllowedOrigin("http://bread-map.s3-website.ap-northeast-2.amazonaws.com/");
		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
		corsConfigSource.registerCorsConfiguration("/**", configuration);
		return corsConfigSource;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/docs/**");
		//        web.ignoring().antMatchers("/v1/auth/valid", "/v1/auth/login", "/v1/auth/register", "/v1/auth/reissue");
		//        web.ignoring().antMatchers("/v1/admin/join", "/v1/admin/login", "/v1/admin/reissue", "/v1/admin/test");
		//        web.ignoring().antMatchers("/h2-console/**", "/favicon.ico", "/v1/actuator/health");
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		web.httpFirewall(defaultHttpFirewall());
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() { //더블 슬래시 허용
		return new DefaultHttpFirewall();
	}
}
