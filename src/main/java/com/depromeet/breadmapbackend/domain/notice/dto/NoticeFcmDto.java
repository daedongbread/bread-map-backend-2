package com.depromeet.breadmapbackend.domain.notice.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeFcmDto {
	private final List<String> fcmTokens;
	private final String title;
	private final String content;
	private final Long contentId;
	private final Long subContentId;
	private final NoticeType type;
	private final String extraParam;

	@Builder
	public NoticeFcmDto(
		final List<String> fcmTokens,
		final String title,
		final String content,
		final Long contentId,
		final Long subContentId,
		final NoticeType type,
		final String extraParam
	) {
		this.fcmTokens = fcmTokens;
		this.title = title;
		this.content = content;
		this.contentId = contentId;
		this.subContentId = subContentId;
		this.type = type;
		this.extraParam = extraParam;
	}

	@Override
	public String toString() {
		return "NoticeFcmDto{" +
				"fcmTokens=" + fcmTokens +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", contentId=" + contentId +
				", subContentId=" + subContentId +
				", type=" + type +
				", extraParam='" + extraParam + '\'' +
				'}';
	}
}
