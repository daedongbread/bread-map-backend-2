package com.depromeet.breadmapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableAsync
@EnableFeignClients
@SpringBootApplication
public class BreadMapBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreadMapBackendApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

//	@PostConstruct
//	public void started(){
//		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
//	}
}
