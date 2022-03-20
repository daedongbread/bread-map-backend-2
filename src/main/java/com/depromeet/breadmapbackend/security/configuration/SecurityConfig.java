package com.depromeet.breadmapbackend.security.configuration;

import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.filter.TokenAuthenticationFilter;
import com.depromeet.breadmapbackend.security.handler.OAuth2AuthenticationSuccessHandler;
import com.depromeet.breadmapbackend.security.properties.AppProperties;
import com.depromeet.breadmapbackend.security.properties.CorsProperties;
import com.depromeet.breadmapbackend.security.service.CustomOAuth2UserService;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/*
 * Created by ParkSuHo by 2022/03/18.
 */

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppProperties appProperties;
    private final CorsProperties corsProperties;

    private final CustomOAuth2UserService oAuth2UserService;

    // 토큰 프로바이더 설정
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(appProperties);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider());
    }

    // 토큰 필터 설정
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(jwtTokenProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().configurationSource(corsConfigurationSource())

                .and()

                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .authorizeRequests()
                .antMatchers("/user/auth/**").permitAll()
                .antMatchers("/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                .antMatchers("/**").hasAnyAuthority(RoleType.USER.getCode())
                .anyRequest().authenticated()

                .and()
                .oauth2Login()
                .successHandler(authenticationSuccessHandler())
                .userInfoEndpoint().userService(oAuth2UserService);

        httpSecurity.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // Cors 설정
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/docs/**");
    }

}
