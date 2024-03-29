package com.depromeet.breadmapbackend.domain.notice.dto;

import java.time.LocalDateTime;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeDto {
	private Long noticeId;
	private String image;
	private String title;
	private Long contentId;
	private String content;
	private String contentParam;
	private Boolean isFollow;
	private Long subContentId;
	private LocalDateTime createdAt;
	private NoticeType noticeType;
	private String extraParam;

	@Builder
	public NoticeDto(String image, Boolean isFollow, Notice notice, String title) {
		this.noticeId = notice.getId();
		this.contentParam = notice.getContentParam();
		this.image = image;
		this.title = title;
		this.contentId = notice.getContentId();
		this.content = notice.getContent();
		this.isFollow = isFollow;
		this.createdAt = notice.getCreatedAt();
		this.noticeType = notice.getType();
		this.subContentId = notice.getSubContentId();
		this.extraParam = notice.getExtraParam();
	}
}
