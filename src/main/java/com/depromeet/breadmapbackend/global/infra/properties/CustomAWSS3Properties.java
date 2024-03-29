package com.depromeet.breadmapbackend.global.infra.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Validated
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class CustomAWSS3Properties {
	@NotBlank(message = "해당값은 필수 값입니다")
	private final String bucket;
	@NotBlank(message = "해당값은 필수 값입니다")
	private final String cloudFront;
	@NotNull(message = "해당값은 필수 값입니다")
	private final DefaultBucket defaultBucket;
	@NotNull(message = "해당값은 필수 값입니다")
	private final DefaultImage defaultImage;

	@Getter
	@RequiredArgsConstructor
	public static class DefaultBucket {
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String image;
	}

	@Getter
	@RequiredArgsConstructor
	public static class DefaultImage {
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String bakery;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String comment;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String like;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String report;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String flag;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String user;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String curation;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String event;
		@NotBlank(message = "해당값은 필수 값입니다")
		private final String breadAdd;
	}
}
