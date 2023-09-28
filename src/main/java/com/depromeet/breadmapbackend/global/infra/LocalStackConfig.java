package com.depromeet.breadmapbackend.global.infra;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile({"default", "local"})
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LocalStackConfig {
	private static final DockerImageName LOCAL_STACK_IMAGE = DockerImageName.parse("localstack/localstack");
	private final CustomAWSS3Properties customAWSS3Properties;

	// GenericContainer start(), stop() 메서드로 생명주기 설정
	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer(){
		return new LocalStackContainer(LOCAL_STACK_IMAGE).withServices(S3).withServices(SQS);
	}

	@Bean
	public AmazonS3Client amazonS3(LocalStackContainer localStackContainer){
		AmazonS3 amazonS3 = AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
			.withCredentials(localStackContainer.getDefaultCredentialsProvider())
			.build();
		amazonS3.createBucket(customAWSS3Properties.getBucket());
		log.info("Connect To LocalStack S3");
		return (AmazonS3Client)amazonS3;
	}

	@Bean
	public AmazonSQSAsync amazonSQS(LocalStackContainer localStackContainer) {
		AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
			.withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SQS))
			.withCredentials(localStackContainer.getDefaultCredentialsProvider())
			.build();
		// amazonSQSAsync.createQueue(); TODO
		log.info("Connect To LocalStack SQS");
		return amazonSQSAsync;
	}

	@Bean
	@Primary
	public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
		return new QueueMessagingTemplate(amazonSQSAsync);
	}
}
