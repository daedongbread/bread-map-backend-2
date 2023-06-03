package com.depromeet.breadmapbackend.domain.notice.dto;

import com.depromeet.breadmapbackend.domain.notice.NoticeType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeFcmDto {
	private final Long userId;
	private final String title;
	private final String content;
	private final Long contentId;
	private final NoticeType type;

	@Builder
	public NoticeFcmDto(
		final Long userId,
		final String title,
		final String content,
		final Long contentId,
		final NoticeType type
	) {
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.contentId = contentId;
		this.type = type;
	}
}
