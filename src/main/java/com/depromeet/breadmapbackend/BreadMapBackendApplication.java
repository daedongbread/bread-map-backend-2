package com.depromeet.breadmapbackend;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@EnableAsync
@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication

@RequiredArgsConstructor
public class BreadMapBackendApplication {

	private final JwtTokenProvider jwtTokenProvider;

	@PostConstruct
	void init() {
		JwtToken jwtToken = jwtTokenProvider.createJwtToken("GOOGLE_115696112574904205385", "ROLE_USER");
		final JwtToken jwtToken1 = jwtTokenProvider.createJwtToken("Deadong01", "ROLE_ADMIN");

		System.out.println("jwtToken.getAccessToken() ============================== " + jwtToken.getAccessToken());
		System.out.println("jwtToken.getAccessToken1() ============================== " + jwtToken1.getAccessToken());

	}

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

}
