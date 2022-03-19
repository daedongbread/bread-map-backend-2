package com.depromeet.breadmapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BreadMapBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreadMapBackendApplication.class, args);
	}

}
