package com.depromeet.breadmapbackend;

import com.depromeet.breadmapbackend.security.properties.AppProperties;
import com.depromeet.breadmapbackend.security.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({CorsProperties.class, AppProperties.class})
public class BreadMapBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreadMapBackendApplication.class, args);
	}

}
