package com.depromeet.breadmapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BreadMapBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreadMapBackendApplication.class, args);
	}

}
