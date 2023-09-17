package com.depromeet.breadmapbackend.global.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile({"stage", "prod"})
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AmazonSQSConfig {
	@Value("${cloud.aws.region.static}")
	private final String region;

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAws() {
		log.info("Connect to SQS");
		return AmazonSQSAsyncClientBuilder.standard()
			.withRegion(region)
			.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
			.build();
	}

	@Bean
	public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
		return new QueueMessagingTemplate(amazonSQSAsync);
	}
}
