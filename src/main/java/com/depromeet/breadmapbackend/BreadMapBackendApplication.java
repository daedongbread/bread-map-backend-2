package com.depromeet.breadmapbackend;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableAsync
@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
//@RequiredArgsConstructor
public class BreadMapBackendApplication {

    //	private final JwtTokenProvider jwtTokenProvider;
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BreadMapBackendApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//	@PostConstruct
//	public PasswordEncoder passwordEncoder() {
//		JwtToken adminUserForEventPost = jwtTokenProvider.createJwtToken("KAKAO_2811166920", RoleType.USER.getCode());
//		System.out.println("passwordEncoder :: ================" + adminUserForEventPost.getAccessToken());
//
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//	}

}
