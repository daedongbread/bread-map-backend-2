package com.depromeet.breadmapbackend.domain.notice.content;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;

public interface NoticeContent {

	boolean support(NoticeType noticeType);

	String getImage(Notice notice);

	String getTitle(Notice notice);

	default String getNickName(final Notice notice) {
		return notice.getFromUser().getNickName();
	}
}
