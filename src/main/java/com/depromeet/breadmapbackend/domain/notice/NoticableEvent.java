package com.depromeet.breadmapbackend.domain.notice;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticableEvent {

	private final Boolean isAlarmOn;
	private final Long userId;
	private final Long fromUserId;
	private final Long contentId;
	private final String content;
	private final NoticeType noticeType;

	@Builder
	public NoticableEvent(
		final Boolean isAlarmOn,
		final Long userId,
		final Long fromUserId,
		final Long contentId,
		final String content,
		final NoticeType noticeType
	) {

		this.isAlarmOn = isAlarmOn;
		this.userId = userId;
		this.fromUserId = fromUserId;
		this.contentId = contentId;
		this.content = content;
		this.noticeType = noticeType;
	}
}
