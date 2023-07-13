package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeEvent {

	private final Boolean isAlarmOn;
	private final User user;
	private final User fromUser;
	private final Long contentId;
	private final String content;
	private final NoticeType noticeType;

	@Builder
	public NoticeEvent(
		final Boolean isAlarmOn,
		final User user,
		final User fromUser,
		final Long contentId,
		final String content,
		final NoticeType noticeType
	) {

		this.isAlarmOn = isAlarmOn;
		this.user = user;
		this.fromUser = fromUser;
		this.contentId = contentId;
		this.content = content;
		this.noticeType = noticeType;
	}

	public Notice toNotice() {
		return Notice.builder()
			.user(this.user)
			.fromUser(this.fromUser)
			.contentId(this.contentId)
			.content(this.content)
			.type(this.noticeType)
			.build();
	}
}
