package com.depromeet.breadmapbackend.domain.admin.feed.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonFeedRequestDto {

	private String subTitle;

	private String introduction;

	private String conclusion;

	@NotBlank(message = "카테고리는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private Long categoryId;

	@NotBlank(message = "피드 이미지는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private String thumbnailUrl;

	@NotBlank(message = "피드 활성화여부는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private FeedStatus activated;

	@NotBlank(message = "피드 타입은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private FeedType feedType;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotBlank(message = "게시 시간은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private LocalDateTime activeTime;

	@Builder
	public CommonFeedRequestDto(String subTitle, String introduction, String conclusion, String thumbnailUrl,
		FeedStatus activated, Long categoryId, FeedType feedType, LocalDateTime activeTime) {
		this.subTitle = subTitle;
		this.categoryId = categoryId;
		this.introduction = introduction;
		this.conclusion = conclusion;
		this.thumbnailUrl = thumbnailUrl;
		this.activated = activated;
		this.feedType = feedType;
		this.activeTime = activeTime;
	}
}
