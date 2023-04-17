package com.depromeet.breadmapbackend.global.infra;

import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.CustomAccessDeniedHandler;
import com.depromeet.breadmapbackend.global.security.CustomAuthenticationEntryPoint;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.filter.JwtAuthenticationFilter;
import com.depromeet.breadmapbackend.global.security.handler.OAuth2AuthenticationSuccessHandler;
import com.depromeet.breadmapbackend.global.security.service.CustomOAuth2UserService;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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

/*
 * Created by ParkSuHo by 2022/03/18.
 */

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final StringRedisTemplate redisTemplate;
    private final CustomRedisProperties customRedisProperties;
    private final UserRepository userRepository;

    // 토큰 프로바이더 설정
//    @Bean
//    public JwtTokenProvider jwtTokenProvider() {
//        return new JwtTokenProvider(appProperties);
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider, refreshTokenRepository);
//    }

    // 토큰 필터 설정
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter(jwtTokenProvider);
//    }

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
                .antMatchers("/v1/auth/valid", "/v1/auth/login", "/v1/auth/register", "/v1/auth/reissue", "/exception/**").permitAll()
                .antMatchers("/v1/admin/join", "/v1/admin/login", "/v1/admin/reissue", "/v1/admin/test").permitAll()
                .antMatchers("/h2-console/**", "/favicon.ico", "/actuator/health").permitAll()
                .antMatchers("/v1/bakeries/**", "/v1/flags/**", "/v1/reviews/**", "/v1/users/**", "/v1/notices/**", "/v1/search/**", "/v1/images/**", "/v1/auth/**").hasAuthority(RoleType.USER.getCode())
                .antMatchers("/v1/admin/**").hasAuthority(RoleType.ADMIN.getCode())
//                .antMatchers("/**").hasAnyAuthority(RoleType.USER.getCode())
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().denyAll()
//                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login()
                .successHandler(
                        new OAuth2AuthenticationSuccessHandler(jwtTokenProvider, redisTemplate, customRedisProperties.getKey().getRefresh(), userRepository))
                .userInfoEndpoint().userService(oAuth2UserService);
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
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/docs/**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        web.httpFirewall(defaultHttpFirewall());
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() { //더블 슬래시 허용
        return new DefaultHttpFirewall();
    }
}
